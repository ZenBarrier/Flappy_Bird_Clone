package com.zenbarrier.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.Random;

class FlappyBird extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
    private Texture[] birds;
    private Texture topTube, bottomTube;
    private int flapState = 0;
    private float birdY = 0;
    private float velocity = 0;
    private int gameState = 0;
    private float gravity = 2;
    private int gap = 400;
    private Random randomGenerator;
    private float tubeVelocity = 5;
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
        bottomTube = new Texture("bottomtube.png");
        topTube = new Texture("toptube.png");
        birds = new Texture[2];
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birdY = Gdx.graphics.getHeight()/2 - birds[0].getHeight()/2;
        randomGenerator = new Random();
        distanceBetweenTubes = birds[0].getWidth()*5;
        for(int i = 0 ; i < numberOfTubes ; i++){
            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
            tubeX[i] = Gdx.graphics.getWidth()/2 - topTube.getWidth()/2 + i*distanceBetweenTubes;
        }
    }

	@Override
	public void render () {

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState!=0) {

            if(Gdx.input.justTouched() && birdY < Gdx.graphics.getHeight()){
                velocity=-35;
            }

            for(int i = 0 ; i < numberOfTubes ; i++){

                if(tubeX[i] <= -topTube.getWidth()){
                    tubeX[i] = tubeX[i] + numberOfTubes*distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);
                }
                else{
                    tubeX[i]-=tubeVelocity;
                }

                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight()/2+gap/2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight()/2 - bottomTube.getHeight()-gap/2 + tubeOffset[i]);
            }



            if(birdY > 0 || velocity<0){
                velocity+=gravity;
                birdY -= velocity;
            }
        }
        else{
            if(Gdx.input.justTouched()){
                gameState = 1;
                velocity = -40;
            }
        }

        batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
        batch.end();
        flapState = 1 - flapState;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
