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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private float jumpVelocity = 380f; // 🔹 Potencia del salto
    private boolean isMoving = false;
    private boolean isJumping = false; // 🔹 Controla si está en el aire
    private float invulnerabilityTime = 2.0f; // 🔹 1 segundo de invulnerabilidad tras una colisión
    private float invulnerabilityTimer = 0;   // 🔹 Temporizador de invulnerabilidad
    private boolean isInvulnerable = false;   // 🔹 Indica si el jugador está en modo invulnerable
    private ShapeRenderer shapeRenderer;

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
    private int score = 0;
    private HUD hud;
    private Rectangle platform1;
    private Rectangle platform2;
    private Rectangle platform3;
    private Rectangle platform4;
    private Rectangle platform5;
    private float gameOverTimer = 0;
    private boolean gameOverTriggered = false;

    public GameScreen(MainGame game) {
        this.game = game;
        this.batch = game.batch;
        this.hud = new HUD();
        this.shapeRenderer = new ShapeRenderer();

        // Cargar imagen de fondo
        background = new Texture("environment/background2.png");

        // **Cargar animaciones**
        idleTexture = new Texture("character/BlueWizard/2BlueWizardIdle/Chara - BlueIdle00000.png");
        walkAnimation = loadWalkAnimation();

        // **Ubicar al personaje en la izquierda Y EN EL SUELO**
        float scaledWidth = idleTexture.getWidth() * playerScale * 0.6f;
        float scaledHeight = idleTexture.getHeight() * playerScale * 0.8f;
        player = new Rectangle(-680, groundLevel, scaledWidth, scaledHeight);
        velocity = new Vector2(0, 0);

        // **Definir la plataforma 1, 2**
        platform1 = new Rectangle(-420, -220, 50, 20);
        platform2 = new Rectangle(-200, -220, 50, 20);
        platform3 = new Rectangle(-600, -65, 400, 20);
        platform4 = new Rectangle(190, -180, 250, 20);
        platform5 = new Rectangle(440, -65, 380, 20);

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
            System.out.println("⚠ ARRIBA coordenadas: "+player.y);
        }

        // **Si el personaje está entre los límites y en el suelo, inicia la caída**
        if (player.x >= -112.59681 && player.x <= 131.93234 && player.y <= groundLevel) {
            //System.out.println("⚠ El personaje entró en la zona de caída. Iniciando caída al vacío...");
            velocity.y = -700; // 🔹 Aplica una velocidad negativa fuerte para simular la caída
        }


        // **Aplicar gravedad en todo momento**
        velocity.y += gravity * delta;
        player.y += velocity.y * delta;

        // **Detectar colisión con plataformas solo si el jugador está cayendo y está por encima de ellas**
        if (velocity.y <= 0) {
            if (player.overlaps(platform1) && player.y > platform1.y) {
                player.y = platform1.y + platform1.height;
                velocity.y = 0;
                isJumping = false;
            } else if (player.overlaps(platform2) && player.y > platform2.y) {
                player.y = platform2.y + platform2.height;
                velocity.y = 0;
                isJumping = false;
            } else if (player.overlaps(platform3) && player.y > platform3.y) {
                player.y = platform3.y + platform3.height;
                velocity.y = 0;
                isJumping = false;
            } else if (player.overlaps(platform4) && player.y > platform4.y) {
                player.y = platform4.y + platform4.height;
                velocity.y = 0;
                isJumping = false;
            } else if (player.overlaps(platform5) && player.y > platform5.y) {
                player.y = platform5.y + platform5.height;
                velocity.y = 0;
                isJumping = false;
            }
        }

        // **Evitar que atraviese el suelo si no está en una plataforma o SOLO si no está en la zona de caída**
        if (player.y <= groundLevel &&
            !player.overlaps(platform1) &&
            !player.overlaps(platform2) &&
            !player.overlaps(platform3) &&
            !player.overlaps(platform4) &&
            !player.overlaps(platform5) &&
            !(player.x >= -112.59681 && player.x <= 85.93234)) {
            player.y = groundLevel;
            velocity.y = 0;
            isJumping = false;
        }

        // **Verificar si cae al vacío y activar GAME OVER con retraso**
        if (player.y <= fallGroundLevel) {
            if (!gameOverTriggered) { // Solo activar una vez
                System.out.println("¡Game Over! El jugador cayó al vacío.");
                hud.setGameOver(true);
                gameOverTriggered = true;
                gameOverTimer = 2.0f; // Retraso de 2 segundos antes de volver al menú
            }
        }

        // **Manejo del temporizador de Game Over**
        if (gameOverTriggered) {
            gameOverTimer -= delta;
            if (gameOverTimer <= 0) {
                game.setScreen(new MenuScreen(game)); // Volver al menú tras 2 segundos
            }
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

        // Actualizar el temporizador de invulnerabilidad
        if (isInvulnerable) {
            invulnerabilityTimer -= delta;
            if (invulnerabilityTimer <= 0) {
                isInvulnerable = false;
            }
        }

        // **Detectar colisión con el Slime**
        for (SlimeEnemy slime : slimeEnemies) {
            if (!isInvulnerable && slime.checkCollision(player)) {  // Solo colisiona si NO es invulnerable
                if (player.y > slime.getY() + slime.getHeight() * 0.5f) { // 🔹 Si cae sobre el slime
                    slimeEnemies.removeValue(slime, true); // 🔹 Eliminar slime
                    hud.increaseScore();
                    System.out.println("✅ Slime eliminado. Puntuación: " + score);
                    velocity.y = jumpVelocity * 0.8f; // 🔹 Rebote tras eliminar slime
                } else {
                    HUD.increaseCollisions();
                    System.out.println("⚠ El jugador ha chocado con el Slime! Intento: " + hud.getCollisionCount() + "/3");
                    isInvulnerable = true;
                    invulnerabilityTimer = invulnerabilityTime;
                    if (hud.getCollisionCount() >= 3) {
                        System.out.println("¡GAME OVER! El jugador ha chocado 3 veces con el Slime.");
                        hud.setGameOver(true);
                        //game.setScreen(new MenuScreen(game));
                        if (!gameOverTriggered) {
                            gameOverTriggered = true;
                            gameOverTimer = 2.0f; // Retraso de 2 segundos
                        }
                        HUD.resetCollisions();
                    }
                }
            }
        }

        // Manejo del temporizador de Game Over
        if (gameOverTriggered) {
            gameOverTimer -= delta;
            if (gameOverTimer <= 0) {
                game.setScreen(new MenuScreen(game)); // Volver al menú tras 2 segundos
            }
        }

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

        // Renderizar el HUD en GameScreen
        hud.render(batch);

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
        shapeRenderer.dispose();
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
