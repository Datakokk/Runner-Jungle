package io.github.gameRunner;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class HUD {
    private static int score;
    private static int collisionCount; // Contador de colisiones con enemigos
    private boolean gameOver;
    private BitmapFont font;

    public HUD(){
        score = 0;
        collisionCount = 0;
        gameOver = false;
        font = new BitmapFont();
        font.setColor(Color.WHITE); // Color blanco para mejor visibilidad
        font.getData().setScale(2f); // Hacer el texto más grande
    }

    public static void increaseScore(){
        score++;
    }

    public static void increaseCollisions(){
        collisionCount++;
    }

    public static void resetCollisions() {
        collisionCount = 0;
    }

    public void setGameOver(boolean GO){
        gameOver = GO;
    }

    public int getScore(){
        return score;
    }

    public int getCollisionCount(){
        return collisionCount;
    }

    public static void setScore(int sc){
        score = sc;
    }

    public boolean isGameOver(){
        return gameOver;
    }

    public void render(SpriteBatch batch){
        batch.begin();
        font.draw(batch, "Score: " + score, 20, Gdx.graphics.getHeight() - 40);
        font.draw(batch, "Collisions: " + collisionCount, 20, Gdx.graphics.getHeight() - 80);

        if(gameOver){
            font.draw(batch, "GAME OVER", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f);
        }

        batch.end();
    }
}
