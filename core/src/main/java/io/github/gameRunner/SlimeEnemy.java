package io.github.gameRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class SlimeEnemy {
    private float x, y;
    private float speed = 120;
    private Rectangle enemyArea;
    private float scale = 0.25f;
    private Animation<TextureRegion> slimeAnimation;
    private float stateTime = 0;  // 🔹 Controla el tiempo de animación

    public SlimeEnemy(float startX, float startY, String slimeType) {
        this.x = startX;
        this.y = startY;
        this.slimeAnimation = loadSlimeAnimation(slimeType);

        float scaledWidth = slimeAnimation.getKeyFrame(0).getRegionWidth() * scale;
        float scaledHeight = slimeAnimation.getKeyFrame(0).getRegionHeight() * scale;
        this.enemyArea = new Rectangle(x, y, scaledWidth, scaledHeight);
    }

    private Animation<TextureRegion> loadSlimeAnimation(String slimeType) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i <= 29; i++) {
            String filePath = String.format("enemies/%s/%s_%05d.png", slimeType, slimeType, i);
            frames.add(new TextureRegion(new Texture(Gdx.files.internal(filePath))));
        }
        return new Animation<>(0.1f, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        x -= speed * delta;
        enemyArea.setPosition(x, y);
        stateTime += delta;  // 🔹 Actualizar el tiempo de animación
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = slimeAnimation.getKeyFrame(stateTime, true);
        batch.draw(currentFrame, x, y, currentFrame.getRegionWidth() * scale, currentFrame.getRegionHeight() * scale);
    }

    public boolean checkCollision(Rectangle player) {
        return enemyArea.overlaps(player);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return enemyArea.getWidth(); }
    public float getHeight() { return enemyArea.getHeight(); }

    public void resetPosition() {
        this.x = Gdx.graphics.getWidth();
    }
}
