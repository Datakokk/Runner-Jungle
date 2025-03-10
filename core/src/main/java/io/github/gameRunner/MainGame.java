package io.github.gameRunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class MainGame extends Game {
    public SpriteBatch batch;
    private Texture image;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
