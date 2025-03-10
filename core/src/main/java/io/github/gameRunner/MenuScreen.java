package io.github.gameRunner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MenuScreen implements Screen {
    private MainGame game;
    private Stage stage;
    private Skin skin;
    private TextButton startButton, exitButton;
    private Texture background;

    private OrthographicCamera camera;
    private Viewport viewport;

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

        // Crear skin y botones
        skin = new Skin();
        createButtons(skin); // 🔹 Llamamos a la nueva función para crear los botones con el estilo personalizado

        // Agregar eventos a los botones
        startButton.addListener(new ClickListener() {
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

        // Organizar los botones en una tabla
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(startButton).padBottom(20).row();
        table.add(exitButton).padBottom(20).row();

        stage.addActor(table);
    }

    /**
     * Función para crear los botones con fuente más grande, color negro y negrita
     */
    private void createButtons(Skin skin) {
        // **Crear una nueva fuente más grande y en negrita**
        BitmapFont font = new BitmapFont();
        font.getData().setScale(2.0f); // 🔹 Aumentar el tamaño de la fuente

        // **Definir el estilo del botón**
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.BLACK; // 🔹 Color negro

        // **Agregar la fuente al skin**
        skin.add("default-font", font);

        // **Crear los botones con el estilo actualizado**
        startButton = new TextButton("START", buttonStyle);
        exitButton = new TextButton("EXIT", buttonStyle);

        // **Definir tamaño de los botones**
        startButton.setSize(200, 80);
        exitButton.setSize(200, 80);
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
        skin.dispose();
        background.dispose();
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
