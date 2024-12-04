package bricker.brick_strategeis;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

public abstract class CollisionStrategyDecorator implements CollisionStrategy {
    protected CollisionStrategy strategy;
    BrickerGameManager gameManager;

    public CollisionStrategyDecorator(CollisionStrategy strategy) {
        this.strategy = strategy;
        this.gameManager = strategy.getGameManager();
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        strategy.onCollision(object1, object2);
    }

    @Override
    public BrickerGameManager getGameManager() {
        return gameManager;
    }
}
