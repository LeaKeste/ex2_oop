package bricker.main;

import bricker.brick_strategeis.*;
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
    private static final int MAIN_BALL_RADIUS = 20;

    private final int initialBrickRows;
    private final int initialBrickCols;
    private Vector2 windowDimensions;
    private WindowController windowController;
    private Ball ball;
    private Paddle userPaddle;
    private danogl.util.Counter brickCount;
    private UserInputListener inputListener;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private Sound collisionSound;
    Random rand;
    Renderable paddleImage;
    private boolean extraPaddleExists;
    private Paddle extraPaddle;
    private int maxExtraPaddleCollisions;

    //constructor
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions, int initialBrickRows, int initialBrickCols) {
        super(windowTitle, windowDimensions);
        this.initialBrickRows = initialBrickRows;
        this.initialBrickCols = initialBrickCols;
        brickCount = new Counter(initialBrickRows * initialBrickCols);
        rand = new Random();
        extraPaddleExists = false;
        extraPaddle = null;
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
// set fields
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.soundReader = soundReader;

        this.paddleImage = imageReader.readImage(
                "assets/paddle.png", true);
        this.collisionSound = soundReader.readSound("assets/blop.wav");
        //create initial objects

        createMainBall();
        createPaddle();
        createBoarders();
        createBackground();
        createBricks();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        checkWin();
        keepPaddleInBoard();
//        todo: erase pucks
        if (extraPaddleExists && extraPaddle.getCollisionCounter() == maxExtraPaddleCollisions){
            removeExtraPedal();
        }
    }

    private void keepPaddleInBoard(){
        Paddle[] paddles = {userPaddle};
        if (extraPaddleExists){
            paddles = new Paddle[] {userPaddle, extraPaddle};
        }
        for (Paddle paddle : paddles) {
            if (paddle.getTopLeftCorner().x() < 0) {
                Vector2 newCoords = new Vector2(0, paddle.getTopLeftCorner().y());
                paddle.setTopLeftCorner(newCoords);
            } else if (paddle.getTopLeftCorner().x() >
                    windowDimensions.x() - paddle.getDimensions().x()) {
                Vector2 newCoords = new Vector2(windowDimensions.x() - paddle.getDimensions().x(),
                        paddle.getTopLeftCorner().y());
                paddle.setTopLeftCorner(newCoords);
            }
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
        ball = new Ball(new Vector2(0,0),
                new Vector2(MAIN_BALL_RADIUS, MAIN_BALL_RADIUS), ballImage, collisionSound);
        windowDimensions = windowController.getWindowDimensions();
        ball.setCenter(windowDimensions.mult(0.5F));
        ball.setTag("Main Ball");
        this.setRandomBallDirection();
        this.gameObjects().addGameObject(ball, Layer.DEFAULT);
    }

    private void setRandomBallDirection(){
        float ballVelX = BALL_SPEED;
        float ballVelY= BALL_SPEED;
        if (rand.nextBoolean()){
            ballVelX *= -1;
        }
        if (rand.nextBoolean()){
            ballVelY *= -1;
        }
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
    }

    private void createPaddle(){
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
        BasicCollisionStrategy basicStrategy = new BasicCollisionStrategy(this);
        float brickWidth = (windowDimensions.x()/ initialBrickRows) - 5;
        for (int i = 0; i < initialBrickRows; i++){
            for (int j = 0; j < initialBrickCols; j++){
                CollisionStrategy strategy = createStrategy(rand, basicStrategy, 0);
                GameObject brick = new Brick(new Vector2(i*(brickWidth + 5), j*(BRICK_HEIGHT + 3)),
                        new Vector2(brickWidth, BRICK_HEIGHT), brickImage, strategy);
                this.gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
            }
        }

    }

    private CollisionStrategy createStrategy(Random random, CollisionStrategy baseStrategy, int decoratorAmount){
        int randomVal = random.nextInt(10);
//        create strategy
        CollisionStrategy strategy;
        if (randomVal == 0){
             strategy = new TurboCollisionDecorator(baseStrategy);
             decoratorAmount++;
        }
//        todo: add more cases
        else if (randomVal == 1){
             strategy = new addPucksCollisionDecorator(baseStrategy);
             decoratorAmount++;

        }
        else if (randomVal == 2){
            strategy = new addPaddleCollisionDecorator(baseStrategy);
            decoratorAmount++;
        }
        else if (randomVal == 3){
            strategy = createStrategy(random, baseStrategy, decoratorAmount);
            decoratorAmount++;
            if (decoratorAmount < 3){
                strategy = createStrategy(random, strategy, decoratorAmount);
                decoratorAmount++;
            }
        }
        else{
            strategy = baseStrategy;
        }
        return strategy;
    }

    public void removeBrick(GameObject brick){
        this.gameObjects().removeGameObject(brick, Layer.STATIC_OBJECTS);
        brickCount.decrement();
    }

    public void applyTurboMode(int Collisions, float turboFactor) {
        Renderable turboBallImage = imageReader.readImage("assets/redball.png",
                true);
        ball.ApplyTurboMode(Collisions, turboFactor, turboBallImage);
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

    public void addPucks(int puckAmount, float sizeFactor, Vector2 pucksLocation) {

        Renderable puckImage = imageReader.readImage("assets/mockBall.png",
                true);
        float puckRadius = MAIN_BALL_RADIUS*sizeFactor;
        Vector2 puckSize = new Vector2(puckRadius, puckRadius);
        for (int i = 0; i < puckAmount; i++){
            Ball puck = new Ball(Vector2.ZERO, puckSize, puckImage, collisionSound);
            puck.setCenter(pucksLocation);
            setPuckDirection(puck);
            this.gameObjects().addGameObject(puck);
//            todo: remove puck if is out of bounds- create puck class?- puck dosent know game boarders
        }
    }

    private void setPuckDirection(Ball puck){
        double angle = rand.nextDouble() * Math.PI;
        float xVel = (float) Math.cos(angle) * BALL_SPEED;
        float yVel = (float) Math.sin(angle) * BALL_SPEED;
        puck.setVelocity(new Vector2(xVel, yVel));

    }

    public void addPaddle(int collisions) {
        if (extraPaddleExists){
            return;
        }
        this.extraPaddle = new Paddle(new Vector2(0, 0),
                new Vector2(100, 15), paddleImage, inputListener);
        extraPaddle.setCenter(new Vector2(windowDimensions.x() / 2, windowDimensions.y()/2));
        this.gameObjects().addGameObject(extraPaddle, Layer.DEFAULT);
        extraPaddleExists = true;
        maxExtraPaddleCollisions = collisions;
    }

    private void removeExtraPedal(){
        this.gameObjects().removeGameObject(extraPaddle);
        extraPaddle = null;
        extraPaddleExists = false;
    }
}
//todo: count once each collision when two balls touch paddle
