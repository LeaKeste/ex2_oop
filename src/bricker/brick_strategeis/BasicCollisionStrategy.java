package bricker.brick_strategeis;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

public class BasicCollisionStrategy implements CollisionStrategy {
    private final BrickerGameManager gameManager;

    public BasicCollisionStrategy(BrickerGameManager gameManager){
        this.gameManager = gameManager;
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        System.out.println("collision with brick detected");
        gameManager.removeBrick(object1);
    }

    @Override
    public BrickerGameManager getGameManager() {
        return this.gameManager;
    }
}
