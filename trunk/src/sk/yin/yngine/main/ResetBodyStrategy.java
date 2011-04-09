package sk.yin.yngine.main;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;
import javax.vecmath.Vector3f;
import sk.yin.yngine.scene.attributes.PhysicsAttribute;
import sk.yin.yngine.util.Log;

/**
 * If an object falls off the ground and reaches certain distance from
 * center of the scene, this strategy moves it back to position at start
 * of animation.
 */
class ResetBodyStrategy implements IGLRendererStrategy {

    GLRenderer outer;

    public ResetBodyStrategy(GLRenderer outer) {
        this.outer = outer;
    }

    public void applyStrategy() {
        Transform t = new Transform();
        for (int i = 0; i < GLRenderer.MODEL_NUM; i++) {
            MotionState ms = outer.motionState[i];
            ms.getWorldTransform(t);
            if (t.origin.lengthSquared() > GLRenderer.WORLD_RADIUS_SQUARED) {
                if (ms instanceof PhysicsAttribute) {
                    Log.log(this.getClass().getName() + " => body resetOrigin");
                    ((PhysicsAttribute) ms).resetToStartOrigin(outer.rigidBodies[i]);
                    outer.rigidBodies[i].setAngularVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
                    outer.rigidBodies[i].setLinearVelocity(new Vector3f(0.0f, 0.0f, 0.0f));
                }
            }
        }
    }
}
