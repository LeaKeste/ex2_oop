package bricker.brick_strategeis;

import danogl.GameObject;
import danogl.util.Vector2;

public class addPucksCollisionDecorator extends CollisionStrategyDecorator{
    final int PUCK_AMOUNT = 2;
    final float SIZE_FACTOR = 0.75f;

    public addPucksCollisionDecorator(CollisionStrategy strategy) {
        super(strategy);
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        strategy.onCollision(object1, object2);
        Vector2 bricksSize = object1.getDimensions();
        gameManager.addPucks(PUCK_AMOUNT, SIZE_FACTOR, object2.getCenter());

    }
}
