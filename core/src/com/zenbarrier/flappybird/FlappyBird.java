package com.zenbarrier.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

class FlappyBird extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture background;
    //private ShapeRenderer shapeRenderer;

    private Texture gameover;

    private Texture[] birds;
    private Texture topTube, bottomTube;
    private int flapState = 0;
    private float birdY = 0;
    private float velocity = 0;
    private Circle birdCircle;

    private int gameState = 0;
    private float gravity = 2;
    private float flyStrength = 30;

    private int gap = 400;
    private Random randomGenerator;
    private float tubeVelocity = 6;
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;
    private Rectangle[] topTubeRectangle = new Rectangle[numberOfTubes];
    private Rectangle[] bottomTubeRectangle = new Rectangle[numberOfTubes];

    private int score;
    private int scoringTube;
    BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
        gameState = 0;
        score = 0;
        scoringTube = 0;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(10);

        background = new Texture("bg.png");
        gameover = new Texture("gameover.png");
        bottomTube = new Texture("bottomtube.png");
        topTube = new Texture("toptube.png");
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;
        randomGenerator = new Random();
        distanceBetweenTubes = birds[0].getWidth() * 5;
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i * distanceBetweenTubes + Gdx.graphics.getWidth();
            topTubeRectangle[i] = new Rectangle();
            bottomTubeRectangle[i] = new Rectangle();
        }
    }

    @Override
    public void render() {

        batch.begin();
        /*shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);*/
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (Gdx.input.justTouched() && birdY < Gdx.graphics.getHeight()) {
                velocity = -flyStrength;
            }

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                score++;
                scoringTube = (scoringTube + 1) % numberOfTubes;
                Gdx.app.log("Score", score + " points");
            }

            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] <= -topTube.getWidth()) {
                    tubeX[i] = tubeX[i] + numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                } else {
                    tubeX[i] -= tubeVelocity;
                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i]);

                topTubeRectangle[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i],
                        topTubeRectangle[i].getWidth(), topTubeRectangle[i].getHeight());

                bottomTubeRectangle[i].set(tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - gap / 2 + tubeOffset[i],
                        bottomTube.getWidth(), bottomTube.getHeight());

                /*
                shapeRenderer.rect(topTubeRectangle[i].x, topTubeRectangle[i].y,
                        topTube.getWidth(),topTube.getHeight());

                shapeRenderer.rect(bottomTubeRectangle[i].x, bottomTubeRectangle[i].y,
                        bottomTubeRectangle[i].getWidth(), bottomTubeRectangle[i].getHeight());*/
            }


            if (birdY > 0 || velocity < 0) {
                velocity += gravity;
                birdY -= velocity;
            } else {
                gameState = 2;
            }
        } else if (gameState == 0) {
            if (Gdx.input.justTouched()) {
                gameState = 1;
                velocity = -flyStrength;
            }
        } else if(gameState == 2){
            batch.draw(gameover, Gdx.graphics.getWidth()/2 - gameover.getWidth()/2,
                    Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);

            if (Gdx.input.justTouched()) {
                create();
                return;
            }
        }

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
        flapState = 1 - flapState;

        font.draw(batch, String.valueOf(score), 100, 200);

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

        /*shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
        shapeRenderer.end();*/
        batch.end();

        for (int i = 0; i < numberOfTubes; i++) {
            if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])) {
                Gdx.app.log("Intersect", "Intersects");
                gameState = 2;
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
