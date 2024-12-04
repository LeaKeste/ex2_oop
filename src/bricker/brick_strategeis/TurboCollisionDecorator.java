package bricker.brick_strategeis;

import bricker.gameobjects.Brick;
import danogl.GameObject;


public class TurboCollisionDecorator extends CollisionStrategyDecorator{
    final int COLLISIONS = 6;
    final float VELOCITY_FACTOR = 1.4f;


    public TurboCollisionDecorator(CollisionStrategy strategy) {
        super(strategy);
    }

    @Override
    public void onCollision(GameObject object1, GameObject object2) {
        strategy.onCollision(object1, object2);
        if (!object2.getTag().equals("Main Ball")) {
            return;
        }
        gameManager.applyTurboMode(COLLISIONS, VELOCITY_FACTOR);
    }
}
