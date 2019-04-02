package com.kerberosstudios.flappybird;

import android.util.Log;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


public class FlappyBird extends ApplicationAdapter {

    private SpriteBatch batch;

	private Texture background;
	private Texture topTube;
	private Texture bottomTube;
	private Texture gameOver;

    private Texture[] opponentAnimation;
	private Texture[] playerAnimation;

    private float playerTwoX = 0;
	private float playerTwoY = 0;
    private float playerOneX = 0;
    private float playerOneY = 0;
    private float playerVelocityX = 4*60;
	private float playerVelocityY = 0;
	private float gravity = 2;
    private float maxTubeOffset;
    private float distanceBetweenTubes;

    private int animationSpeed = 0;
    private int score = 0;
	private int scoringTube = 0;
    private int birdAnimationState = 0;
    private int gameState = 0;
    private int tubeGap = 600;
    private int numberOfTubes = 4;

    private float[] tubeX = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];

	private Rectangle[] topTubesCollisionRects;
    private Rectangle[] bottomTubesCollisionRects;

	private FrameRate frameRate;
	private Random rng;
    private BitmapFont scoreFont;
    private Circle playerCollisionCircle;
    private Socket socket;
    private String roomName = "";

	public FlappyBird(){
    }

	@Override
    /**
     * Initialize method.
     */
	public void create () {
		batch = new SpriteBatch();

		// Create background texture
		background = new Texture("bg.png");

		// Create the player animation list
		playerAnimation = new Texture[2];
		playerAnimation[0] = new Texture("bird.png");
        playerAnimation[1] = new Texture("bird2.png");

        // Create the opponent animation list
        opponentAnimation = new Texture[2];
        opponentAnimation[0] = new Texture("birdShadow.png");
        opponentAnimation[1] = new Texture("birdShadow2.png");

        // Tube textures
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");

        // Game over texture
        gameOver = new Texture("gameOver.png");

        // Create FrameRate object
        frameRate = new FrameRate();

        // Set the maxTubeOffset
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - tubeGap / 2 - 100;

        // Set the distanceBetweenTubes
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

        // Create collision shapes for tubes and player
        topTubesCollisionRects = new Rectangle[numberOfTubes];
        bottomTubesCollisionRects = new  Rectangle[numberOfTubes];
        playerCollisionCircle = new Circle();

        // Initial location of the players
        playerOneY = Gdx.graphics.getHeight() / 2 - playerAnimation[0].getHeight() / 2;
        playerTwoY = Gdx.graphics.getHeight() / 2 - playerAnimation[0].getHeight() / 2 - 200;

        // Create the scoreboard
        scoreFont = new BitmapFont();
        scoreFont.setColor(Color.WHITE);
        scoreFont.getData().setScale(10);

        // Set the tubes position off the screen
        // Will be corrected when matchmaking is ready
        for (int i = 0; i < numberOfTubes; i++) {
            tubeX[i] = Gdx.graphics.getWidth() + topTube.getWidth();
        }

        // Get the socker from the SocketHandler
        socket = SocketHandler.getSocket();
        socket.on("gameData", onGameData);
        socket.on("readyToPlay", onReadyToPlay);


        // Send event to server that indicate that this client is ready to be matched with another player
        socket.emit("startMatchMaking");
	}

    private void createTubes() {
        for (int i = 0; i < numberOfTubes; i++) {
            tubeOffset[i] = (rng.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()  - tubeGap - 200);
            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
            topTubesCollisionRects[i] = new Rectangle();
            bottomTubesCollisionRects[i] = new Rectangle();
        }
    }

    /**
     * Method that generat a long that is use to create the seed for the RNG.
     * @param s
     * @return
     */
    public long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L*hash + c;
        }
        return hash;
    }

    /**
     * Send game over data to the server.
     */
	private void sendGameOverData(){
        JSONObject data = new JSONObject();
        try {
            data.put("score", score);
            data.put("roomName", roomName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("gameOver", data);

    }

    /**
     * Send game data with the position of the player and the current room name.
     */
    private void sendGameData() {
        JSONObject data = new JSONObject();
        try {
            data.put("x", playerOneX);
            data.put("y", playerOneY);
            data.put("roomName", roomName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        socket.emit("sendGameData", data);
    }

    /**
     * Listener for the gameData event
     */
    private Emitter.Listener onGameData = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                playerTwoX = (float)data.getInt("x");
                playerTwoY = (float)data.getInt("y");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Listener for the readyToPlay event.
     */
    private Emitter.Listener onReadyToPlay = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            roomName = (String)args[0];
            Log.d("Roomname", roomName);
            rng = new Random(stringToSeed(roomName));
            createTubes();
            gameState = 1;
        }
    };

    @Override
    /**
     * Frame update method. Updates every frame.
     */
	public void render () {

	    // Get the deltatime
        float deltaTime = Gdx.graphics.getDeltaTime();

        if (gameState == 1){

            sendGameData();

            if (Gdx.input.justTouched()) {
                playerVelocityY = - 30;
            }

            // Update player reference position
            playerOneX += Math.abs(playerVelocityX * deltaTime);

            for (int i = 0; i < numberOfTubes; i++) {

                // If the tube has exited the screen
                // Move it to the right side of the screen so it simulates an endless amount of tubes
                if (tubeX[i] < -topTube.getWidth()){

                    // The new position
                    tubeX[i] += numberOfTubes * distanceBetweenTubes;

                    // Generate a new offset
                    tubeOffset[i] = (rng.nextFloat() - 0.5f) * (Gdx.graphics.getHeight()  - tubeGap - 200);

                } else {
                    // Move the tube
                    tubeX[i] -= (playerVelocityX * deltaTime);

                    // Check if tube has passed the middle of the screen(The players position)
                    // Add one to the score because this means that the player has passed through it
                    if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2){
                        score++;

                        // Increment the scoring index of the tube we are checking
                        if (scoringTube < numberOfTubes - 1){
                            scoringTube++;

                        } else {
                            // If this was the last tube in the array, set it to zero
                            scoringTube = 0;
                        }
                    }
                }
            }

            // If the bird is above the bottom of the screen, let it fall
            // Let the bird fall if user have pressed jump
            if (playerOneY > 0){
                playerVelocityY += gravity;
                playerOneY -= playerVelocityY;
            } else {
                // Game Over
                gameState = 2;
                sendGameOverData();
            }

        } else if (gameState == 0){
            // This is the first gamestate
            // The player is just flying in the middle of the screen and none of the game mechanics are active until the user touch the screen
            /*
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
            */
        } else if (gameState == 2){
            // This is game over
            // If the user touches the screen again, reset the game
            /*
            if (Gdx.input.justTouched()) {
                startGame();
                gameState = 1;
                score = 0;
                scoringTube = 0;
                playerVelocityY = 0;
            }
            */
        }

        // Switch animation every fifth frame
        if(animationSpeed == 4){
            birdAnimationState = ((birdAnimationState == 0) ? 1 : 0);
            animationSpeed = 0;
        } else {
            animationSpeed++;
        }


        // Tells the renderer that we want to start to displaying sprites now
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());



        // Draw the tubes
        for (int i = 0; i < numberOfTubes; i++) {

            batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + tubeGap / 2 + tubeOffset[i]);
            batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - tubeGap / 2 + tubeOffset[i]);

            topTubesCollisionRects[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + tubeGap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
            bottomTubesCollisionRects[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - bottomTube.getHeight() - tubeGap / 2 + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());
        }

        // Draw the players
        batch.draw(opponentAnimation[birdAnimationState], Gdx.graphics.getWidth() / 2 - playerAnimation[birdAnimationState].getWidth() / 2 - (playerOneX - playerTwoX), playerTwoY);
        batch.draw(playerAnimation[birdAnimationState], Gdx.graphics.getWidth() / 2 - playerAnimation[birdAnimationState].getWidth() / 2, playerOneY);

        // Draw the Game Over image if the game is over
        if (gameState == 2){
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
        }

        // Draw the score
        scoreFont.draw(batch, String.valueOf(score), 100 , 200);
        batch.end();

        frameRate.update();
        frameRate.render();

        if (gameState != 2){
            // Set the collision circle for the bird
            playerCollisionCircle.set(Gdx.graphics.getWidth() / 2, playerOneY + playerAnimation[birdAnimationState].getHeight() / 2, playerAnimation[birdAnimationState].getWidth() / 2);


            // Check if the playerCollisionCircle is colliding with any of the tubes collsionRects
            for (int i = 0; i < numberOfTubes; i++) {
                if (Intersector.overlaps(playerCollisionCircle, topTubesCollisionRects[i]) || Intersector.overlaps(playerCollisionCircle, bottomTubesCollisionRects[i])){
                    gameState = 2;
                    sendGameOverData();
                }
            }
        }
	}

    @Override
	public void dispose () {
        socket.off("gameData", onGameData);
        socket.off("readyToPlay", onReadyToPlay);
		batch.dispose();
		background.dispose();
	}
}
