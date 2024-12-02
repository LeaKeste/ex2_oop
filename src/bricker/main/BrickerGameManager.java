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
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.util.Random;
import static java.awt.event.KeyEvent.VK_W;

public class BrickerGameManager extends GameManager {
    private static final float BALL_SPEED = 150;
    private static final int DEFAULT_ROWS = 7;
    private static final int DEFAULT_COLS = 8;
    private static final int BRICK_HEIGHT = 15;

    private final int initialBrickRows;
    private final int initialBrickCols;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private Ball ball;
    private GameObject userPaddle;
    private danogl.util.Counter brickCount;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private SoundReader soundReader;

//constructor
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions, int initialBrickRows, int initialBrickCols) {
        super(windowTitle, windowDimensions);
        this.initialBrickRows = initialBrickRows;
        this.initialBrickCols = initialBrickCols;
        brickCount = new Counter(initialBrickRows * initialBrickCols);
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
// set fields
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;
//create initial objects
        createMainBall();
        createPedal();
        createBoarders();
        createBackground();
        createBricks();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkWin();
        keepPaddleInBoard();

    }

    private void keepPaddleInBoard(){
        if (userPaddle.getTopLeftCorner().x() < 0){
            Vector2 newCoords = new Vector2(0, userPaddle.getTopLeftCorner().y());
            userPaddle.setTopLeftCorner(newCoords);
        }
        else if (userPaddle.getTopLeftCorner().x() >
                windowDimensions.x() - userPaddle.getDimensions().x()){
            Vector2 newCoords = new Vector2(windowDimensions.x() - userPaddle.getDimensions().x(),
                    userPaddle.getTopLeftCorner().y());
            userPaddle.setTopLeftCorner(newCoords);
        }
    }

    private void checkWin(){
        float ballHeight = ball.getCenter().y();
        String prompt = "";
        if ( brickCount.value() == 0 || inputListener.isKeyPressed(VK_W)){
            prompt = "You Win!";

        }
        if (ballHeight > windowDimensions.y()){
            prompt = "You Lose!";
        }
        if (!prompt.isEmpty()){
            prompt += " Play again?";
            if (windowController.openYesNoDialog(prompt)){
                windowController.resetGame();
            }
            else {
                windowController.closeWindow();
            }

        }
    }

    private void createMainBall(){
        Renderable ballImage = imageReader.readImage(
                "assets/ball.png", true);
        Sound collisionSound = soundReader.readSound("assets/blop.wav");
        ball = new Ball(new Vector2(0,0),
                new Vector2(20, 20), ballImage, collisionSound);
        windowDimensions = windowController.getWindowDimensions();
        ball.setCenter(windowDimensions.mult(0.5F));
        this.setRandomBallDirection();
        this.gameObjects().addGameObject(ball, Layer.DEFAULT);
    }

    private void setRandomBallDirection(){
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
    }

    private void createPedal(){
        Renderable paddleImage = imageReader.readImage(
                "assets/paddle.png", true);
        this.userPaddle = new Paddle(new Vector2(0, 0),
                new Vector2(100, 15), paddleImage, inputListener);
        userPaddle.setCenter(new Vector2(windowDimensions.x() / 2, windowDimensions.y()-30));
        this.gameObjects().addGameObject(userPaddle, Layer.DEFAULT);
    }

    private void createBoarders(){
//        left and right
        int[] boarderLeftCorners = {(int)windowDimensions.x(), 0 };
        for (int leftCorner : boarderLeftCorners) {
            GameObject boarder = new GameObject(new Vector2(leftCorner, 0),
                    new Vector2(5, windowDimensions.y()), null);
            this.gameObjects().addGameObject(boarder, Layer.STATIC_OBJECTS);
        }
//        top
        GameObject boarder = new GameObject(new Vector2(0, 0),
                new Vector2(windowDimensions.x(), 5), null);
        this.gameObjects().addGameObject(boarder,  Layer.STATIC_OBJECTS);

    }

    private void createBackground(){
        Renderable backgroundImage = imageReader.readImage(
                "assets/DARK_BG2_small.jpeg", true);
        GameObject background = new GameObject(new Vector2(0, 0),
                new Vector2(windowDimensions.x(), windowDimensions.y()), backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    private void createBricks(){
        Renderable brickImage = imageReader.readImage(
                "assets/brick.png", false);
        BasicCollisionStrategy strategy = new BasicCollisionStrategy(this);
        float brickWidth = (windowDimensions.x()/ initialBrickRows) - 5;

        for (int i = 0; i < initialBrickRows; i++){
            for (int j = 0; j < initialBrickCols; j++){
                GameObject brick = new Brick(new Vector2(i*(brickWidth + 5), j*(BRICK_HEIGHT + 3)),
                        new Vector2(brickWidth, BRICK_HEIGHT), brickImage, strategy);
                this.gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
            }
        }

    }

    public void removeBrick(GameObject brick){
        this.gameObjects().removeGameObject(brick, Layer.STATIC_OBJECTS);
        brickCount.decrement();
    }

    public static void main(String[] args) {
        int rows = DEFAULT_ROWS;
        int cols = DEFAULT_COLS;
        if(args.length == 2){
            rows = Integer.parseInt(args[0]);
            cols = Integer.parseInt(args[1]);
        }
        GameManager gameManager =
                new BrickerGameManager("bouncing ball",
                        new Vector2(700, 500), rows, cols);
        gameManager.run();
    }


}
