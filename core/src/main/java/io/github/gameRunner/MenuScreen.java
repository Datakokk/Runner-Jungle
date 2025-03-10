package io.github.gameRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private Texture background;
    private OrthographicCamera camera;
    private Viewport viewport;

    // Botones con imágenes
    private ImageButton playButton;
    private ImageButton exitButton;

    public MenuScreen(MainGame game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Configurar la cámara y el viewport
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(1280, 720, camera);
        camera.position.set(1280 / 2f, 720 / 2f, 0);
        camera.update();

        // Cargar imagen de fondo
        background = new Texture("environment/inicio.png");

        // Crear los botones con imágenes
        createButtons();

        // Organizar los botones en una tabla
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(playButton).padBottom(20).row();
        table.add(exitButton).padBottom(20).row();

        stage.addActor(table);
    }

    /**
     * Método para crear botones con imágenes
     */
    private void createButtons() {
        Texture playTexture = new Texture("buttons/play.png");
        Texture exitTexture = new Texture("buttons/exit.png");

        // Usamos TextureRegionDrawable para los botones
        playButton = new ImageButton(new TextureRegionDrawable(playTexture));
        exitButton = new ImageButton(new TextureRegionDrawable(exitTexture));

        // Definir tamaño de los botones
        playButton.setSize(200, 80);
        exitButton.setSize(200, 80);

        // **Agregar eventos a los botones**
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game));
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // **Actualizar la cámara y viewport**
        viewport.apply();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.batch.draw(background,
            camera.position.x - camera.viewportWidth / 2,
            camera.position.y - camera.viewportHeight / 2,
            camera.viewportWidth,
            camera.viewportHeight);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
