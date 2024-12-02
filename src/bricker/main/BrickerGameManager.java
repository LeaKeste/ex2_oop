package bricker.main;

import bricker.brick_strategeis.BasicCollisionStrategy;
import bricker.gameobjects.Ball;
import bricker.gameobjects.Brick;
import bricker.gameobjects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.ImageRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

public class BrickerGameManager extends GameManager {


    private static final float BALL_SPEED = 100;

    public BrickerGameManager(String windowTitle, Vector2 windowDimensions){
        super(windowTitle, windowDimensions);
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
//        creating ball
        Renderable ballImage = imageReader.readImage(
                        "assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        GameObject ball = new Ball(new Vector2(0,0),
                new Vector2(20, 20), ballImage, collisionSound);
        Vector2 windowDimensions = windowController.getWindowDimensions();
        ball.setCenter(windowDimensions.mult(0.5F));
        float ballVelX = BALL_SPEED;
        float ballVelY= BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()){
            ballVelX *= -1;
        }
        if (rand.nextBoolean()){
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
        this.gameObjects().addGameObject(ball);

        Renderable paddleImage = imageReader.readImage(
                "assets/paddle.png", true);
//        create user paddle
        GameObject userPaddle = new Paddle(new Vector2(0, 0),
                new Vector2(100, 15), paddleImage, inputListener);
        userPaddle.setCenter(new Vector2(windowDimensions.x() / 2, windowDimensions.y()-30));
        this.gameObjects().addGameObject(userPaddle);
//
//
        createBoarders(windowDimensions);
        createBackground(imageReader, windowDimensions);
        createBricks(imageReader, windowDimensions);
    }

    private void createBoarders(Vector2 windowDimensions){
//        left and right
        int[] boarderLeftCorners = {(int)windowDimensions.x(), 0 };
        for (int leftCorner : boarderLeftCorners) {
            GameObject boarder = new GameObject(new Vector2(leftCorner, 0),
                    new Vector2(5, windowDimensions.y()), null);
            this.gameObjects().addGameObject(boarder);
        }
//        top
        GameObject boarder = new GameObject(new Vector2(0, 0),
                new Vector2(windowDimensions.x(), 5), null);
        this.gameObjects().addGameObject(boarder);

    }

    private void createBackground(ImageReader imageReader, Vector2 windowDimensions){
        Renderable backgroundImage = imageReader.readImage(
                "assets/DARK_BG2_small.jpeg", true);
        GameObject background = new GameObject(new Vector2(0, 0),
                new Vector2(windowDimensions.x(), windowDimensions.y()), backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    private void createBricks(ImageReader imageReader, Vector2 windowDimensions){
        Renderable brickImage = imageReader.readImage(
                "assets/brick.pnj", false);
        BasicCollisionStrategy strategy = new BasicCollisionStrategy();
        GameObject brick = new Brick(new Vector2(0, 0),
                new Vector2(windowDimensions.x(), 15), brickImage, strategy);
        this.gameObjects().addGameObject(brick);
    }

    public static void main(String[] args) {
        GameManager gameManager =
                new BrickerGameManager("bouncing ball",
                        new Vector2(700, 500));
        gameManager.run();


    }
}
