package sk.yin.yngine.main;

import javax.vecmath.Vector3f;
import sk.yin.yngine.util.Log;

/**
 * Forces an object to jump vertically upeards.
 */
class JumpStrategy implements IGLRendererStrategy {

    public final Vector3f JUMP_VECTOR = new Vector3f(0.0F, 125.0F, 0.0F);
    int jumpIdx;
    GLRenderer outer;

    public JumpStrategy(int jumpIdx, GLRenderer outer) {
        this.outer = outer;
        this.jumpIdx = jumpIdx;
    }

    public void applyStrategy() {
        Log.log(this.getClass().getName() + " => jump applyImpulse Central(" + JUMP_VECTOR.toString() + ")");
        outer.rigidBodies[jumpIdx].applyCentralImpulse(JUMP_VECTOR);
    }
}
