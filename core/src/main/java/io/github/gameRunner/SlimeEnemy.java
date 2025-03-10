package io.github.gameRunner;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SlimeEnemy {
    private float x, y;
    private Texture enemyTexture;
    private float speed = 120;  // 🔹 Velocidad de movimiento del slime
    private Rectangle enemyArea;
    private float scale = 0.25f;

    public SlimeEnemy(float startX, float startY, String slimeType) {
        this.x = startX;
        this.y = startY;

        // 🔹 Cargar la imagen correcta del slime según el tipo
        String filePath = "enemies/" + slimeType + "/" + slimeType + "_00001.png";
        enemyTexture = new Texture(Gdx.files.internal(filePath));

        if (enemyTexture == null) {
            System.out.println("⚠ ERROR: No se pudo cargar la textura del slime: " + filePath);
        } else {
            System.out.println("✅ Slime cargado correctamente desde: " + filePath);
        }

        // 🔹 Definir la hitbox del enemigo
        float scaledWidth = enemyTexture.getWidth() * scale;
        float scaleHeight = enemyTexture.getHeight() * scale;
        this.enemyArea = new Rectangle(x, y, scaledWidth, scaleHeight);
    }

    public void update(float delta) {
        x -= speed * delta;  // 🔹 Mueve el slime de derecha a izquierda
        enemyArea.setPosition(x, y);
    }

    public void render(SpriteBatch batch) {
        batch.draw(enemyTexture, x, y, enemyTexture.getWidth() * scale, enemyTexture.getHeight() * scale);
    }

    public boolean checkCollision(Rectangle player) {
        return enemyArea.overlaps(player);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() {
        return enemyTexture.getWidth() * scale; // Devuelve el tamaño escalado
    }

    public void resetPosition() {
        this.x = Gdx.graphics.getWidth() + enemyTexture.getWidth() * scale;  // 🔹 Reiniciar en el extremo derecho de la pantalla
    }
}
