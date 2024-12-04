package bricker.brick_strategeis;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

public interface CollisionStrategy {
    public void onCollision(GameObject object1, GameObject object2);
    BrickerGameManager getGameManager();
}
