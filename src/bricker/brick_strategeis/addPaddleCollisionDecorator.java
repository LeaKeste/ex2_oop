package bricker.brick_strategeis;

import danogl.GameObject;

public class addPaddleCollisionDecorator extends CollisionStrategyDecorator{
    final int COLLISIONS = 4;
    public addPaddleCollisionDecorator(CollisionStrategy strategy) {
        super(strategy);
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        super.onCollision(object1, object2);
        gameManager.addPaddle(COLLISIONS);
    }
}
