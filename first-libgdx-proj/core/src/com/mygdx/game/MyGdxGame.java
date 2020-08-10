package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.actions.RemoveAction;

import java.util.ArrayList;
import java.util.Random;

import sun.rmi.runtime.Log;

public class MyGdxGame extends ApplicationAdapter {
    SpriteBatch batch;
    Texture background;
    Texture[] man;

    int manState = 0;
    int pause = 0;

    float gravity = 1f;
    float velocity = 0;

    int manY = 0;

    Rectangle manRectangle;

    ArrayList<Integer> coinXs = new ArrayList<>();
    ArrayList<Integer> coinYs = new ArrayList<>();
    ArrayList<Rectangle> coinRectangles = new ArrayList<>();

    Texture coin;
    int coinCount;


    ArrayList<Integer> bombXs = new ArrayList<>();
    ArrayList<Integer> bombYs = new ArrayList<>();
    ArrayList<Rectangle> bombRectangles = new ArrayList<>();

    Texture bomb;
    int bombCount;

    Random random;

    int score;

    BitmapFont font;
    int gameState;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
        man = new Texture[5];
        man[0] = new Texture("frame-1.png");
        man[1] = new Texture("frame-2.png");
        man[2] = new Texture("frame-3.png");
        man[3] = new Texture("frame-4.png");
        man[4] = new Texture("dizzy-1.png");


        manY = Gdx.graphics.getHeight() / 2;

        coin = new Texture("coin.png");
        bomb = new Texture("bomb.png");
        random = new Random();

        manRectangle = new Rectangle();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

    }

    public void makeCoin() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        coinYs.add((int) height);
        coinXs.add(Gdx.graphics.getWidth());
    }

    public void makeBomb() {
        float height = random.nextFloat() * Gdx.graphics.getHeight();
        bombYs.add((int) height);
        bombXs.add(Gdx.graphics.getWidth());
    }

    @Override
    public void render() {
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {
            // game is live
            // coins
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }
            coinRectangles.clear();
            for (int i = 0; i < coinXs.size(); i++) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinXs.set(i, coinXs.get(i) - 4);
                coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
            }


            // bombs
            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }
            bombRectangles.clear();
            for (int i = 0; i < bombXs.size(); i++) {

                // bomb
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombXs.set(i, bombXs.get(i) - 8);
                bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
            }


            if (Gdx.input.justTouched()) {
                velocity = -25;
            }

            if (pause < 8) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;

                } else {
                    manState = 0;
                }
            }

            velocity += gravity;

            manY -= velocity;

            if (manY <= 0) {
                manY = 0;
            }


        } else if (gameState == 0) {
            // waiting to start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 2) {
            // game over
            if (Gdx.input.justTouched()) {
                gameState = 1;
                manY = Gdx.graphics.getHeight() / 2;
                score = 0;
                velocity = 0;
                coinXs.clear();
                coinYs.clear();
                bombXs.clear();
                bombYs.clear();
                coinRectangles.clear();
                coinCount = 0;

                bombRectangles.clear();
                bombCount = 0;
            }
        }


        if (gameState == 2){ //game over
            manState = 4; //dizzy
        }
        batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);

        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2,
                manY,
                man[manState].getWidth(),
                man[manState].getHeight());
        // check coin collision
        for (int i = 0; i < coinRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, coinRectangles.get(i))) {
                Gdx.app.log("coin:", "Collision!");
                score++;
                coinRectangles.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
//                break;
            }
        }

        // check bomb collision
        for (int i = 0; i < bombRectangles.size(); i++) {
            if (Intersector.overlaps(manRectangle, bombRectangles.get(i))) {
                Gdx.app.log("bomb:", "Collision!");
                gameState = 2;
            }
        }
        font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();

    }
}
