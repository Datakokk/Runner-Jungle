package io.github.gameRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {
    private MainGame game;
    private SpriteBatch batch;
    private Texture background;
    private Rectangle player;
    private Vector2 velocity;
    private float gravity = -500f;  // 🔹 Gravedad más fuerte para caída más rápida
    private float playerScale = 0.35f;
    private float playerSpeed = 200f;
    private float jumpVelocity = 450f; // 🔹 Potencia del salto
    private boolean isMoving = false;
    private boolean isJumping = false; // 🔹 Controla si está en el aire

    private OrthographicCamera camera;
    private Viewport viewport;

    // **Animaciones**
    private Animation<TextureRegion> walkAnimation;
    private Texture idleTexture;
    private float stateTime = 0;

    // **Suelo y Caída**
    float groundLevel = -345;
    float fallGroundLevel = -700;

    // **Enemigos**
    private float slimeRespawnTimer = 0;
    private Array<SlimeEnemy> slimeEnemies;
    private int collisionCount = 0;
    private HUD hud;
    public GameScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.hud = new HUD();

        // Cargar imagen de fondo
        background = new Texture("environment/background2.png");

        // **Cargar animaciones**
        idleTexture = new Texture("character/BlueWizard/2BlueWizardIdle/Chara - BlueIdle00000.png");
        walkAnimation = loadWalkAnimation();

        // **Ubicar al personaje en la izquierda Y EN EL SUELO**
        float scaledWidth = idleTexture.getWidth() * playerScale;
        float scaledHeight = idleTexture.getHeight() * playerScale;
        player = new Rectangle(-680, groundLevel, scaledWidth, scaledHeight);
        velocity = new Vector2(0, 0);

        // Configurar la cámara
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);

        // **Crear la lista de enemigos**
        slimeEnemies = new Array<>();
    }

    private Animation<TextureRegion> loadWalkAnimation() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i <= 19; i++) {
            String filePath = String.format("character/BlueWizard/2BlueWizardWalk/Chara_BlueWalk%05d.png", i);
            frames.add(new TextureRegion(new Texture(filePath)));
        }
        return new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stateTime += delta;
        slimeRespawnTimer += delta;

        // **Generar nuevos enemigos**
        if (slimeRespawnTimer >= 2 && slimeEnemies.size < 2) {
            slimeEnemies.add(new SlimeEnemy(Gdx.graphics.getWidth(), groundLevel, "SlimeOrange"));
            slimeRespawnTimer = 0; // Reiniciar el temporizador
        }

        // **Actualizar enemigos**
        for (SlimeEnemy slime : slimeEnemies) {
            slime.update(delta);
        }

        // **Movimiento del personaje**
        isMoving = false;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.x += playerSpeed * delta;
            isMoving = true;
            System.out.println("Esta avanzando hacia la derecha" + player.x);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.x -= playerSpeed * delta;
            isMoving = true;
            System.out.println("Esta avanzando hacia la izquierda" + player.x);
        }

        // **Salto**
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !isJumping) {
            velocity.y = jumpVelocity;
            isJumping = true;
        }

        // **Si el personaje está entre los límites y en el suelo, inicia la caída**
        if (player.x >= -112.59681 && player.x <= 20.012892 && player.y <= groundLevel) {
            System.out.println("⚠ El personaje entró en la zona de caída. Iniciando caída al vacío...");
            velocity.y = -700; // 🔹 Aplica una velocidad negativa fuerte para simular la caída
        }


        // **Aplicar gravedad en todo momento**
        velocity.y += gravity * delta;
        player.y += velocity.y * delta;

        // **Evitar que atraviese el suelo SOLO si no está en la zona de caída**
        if (player.y <= groundLevel && !(player.x >= -112.59681 && player.x <= 20.012892)) {
            player.y = groundLevel;
            velocity.y = 0;
            isJumping = false; // 🔹 Ya no está en el aire
        }

        // **Verificar si cae al vacío y activar GAME OVER**
        if (player.y <= fallGroundLevel) {
            System.out.println("¡Game Over! El jugador cayó al vacío.");
            game.setScreen(new MenuScreen(game));
        }

        // **Verificar si el Slime ha salido de la pantalla y reiniciar**
        for (SlimeEnemy slime : slimeEnemies) {
            if (slime.getX() < -slime.getWidth() * 5) {
                slimeRespawnTimer += delta;
                if (slimeRespawnTimer >= 2) {
                    slime.resetPosition();
                    slimeRespawnTimer = 0;
                }
            }
        }

        // **Detectar colisión con el Slime**
        for (SlimeEnemy slime : slimeEnemies) {
            if (slime.checkCollision(player)) {
                HUD.increaseCollisions(); // Aumentar el contador de colisiones en el HUD
                System.out.println("⚠ El jugador ha chocado con el Slime! Intento: " + hud.getCollisionCount() + "/3");

                if (hud.getCollisionCount() >= 3) {
                    System.out.println("¡GAME OVER! El jugador ha chocado 3 veces con el Slime.");
                    hud.setGameOver(true);
                    game.setScreen(new MenuScreen(game));
                    HUD.resetCollisions(); // Reiniciar colisiones para el siguiente intento
                }
            }
        }

        // Renderizar el HUD en GameScreen
        hud.render(batch);

        // **Renderizar todo**
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        // **Dibujar el fondo**
        batch.draw(background,
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight);

        // **Dibujar los enemigos**
        for (SlimeEnemy slime : slimeEnemies) {
            slime.render(batch);
        }

        // **Dibujar el personaje animado**
        TextureRegion currentFrame = isMoving ? walkAnimation.getKeyFrame(stateTime) : new TextureRegion(idleTexture);
        batch.draw(currentFrame, player.x, player.y, player.width, player.height);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        idleTexture.dispose();
        background.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
