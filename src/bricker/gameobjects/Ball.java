package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Ball extends GameObject {
    private final Sound collisionSound;
    private final Renderable image;
    private int collisionCounter;
    private boolean isTurboModeOn;
    private int turboModeBegin;
    int turboModeCollisions;
    float turboFactor;
    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        this.image = renderable;
        collisionCounter = 0;
        isTurboModeOn = false;
        turboModeBegin = 0;
        turboModeCollisions = 0;

    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVel = getVelocity().flipped(collision.getNormal());
        setVelocity(newVel);
        collisionSound.play();
        collisionCounter ++;
    }

    public int getCollisionCounter() {
        return collisionCounter;
    }

    public void ApplyTurboMode(int collisions, float turboFactor, Renderable turboRenderable) {
        if (isTurboModeOn){
            return;
        }
        isTurboModeOn = true;
        turboModeBegin = collisionCounter;
        turboModeCollisions = collisions;
        this.turboFactor = turboFactor;
        setVelocity(getVelocity().mult(turboFactor));
        this.renderer().setRenderable(turboRenderable);
    }

    private void endTurboMode(){
        isTurboModeOn = false;
        setVelocity(getVelocity().mult(1/turboFactor));
        this.renderer().setRenderable(image);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
//        check if turbo mode needs to be turned off
        if (isTurboModeOn) {
            if (collisionCounter == turboModeBegin + turboModeCollisions) {
                endTurboMode();
            }
        }
    }
}
