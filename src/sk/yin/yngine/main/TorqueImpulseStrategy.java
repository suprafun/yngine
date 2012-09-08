package sk.yin.yngine.main;

import javax.vecmath.Vector3f;

import sk.yin.yngine.util.Log;

import com.bulletphysics.linearmath.Transform;

/**
 * Makes forced object hunt a target object by applying torque implulse.
 * The impulses have some level of randomness aplied to make the movements
 * less predictable and more entertaining.
 */
class TorqueImpulseStrategy implements IGLRendererStrategy {

    public static final double RANDOM_SIZE = 1.0;
    public static final double RANDOM_BASE = -RANDOM_SIZE / 2;
    public static final float IMPULSE_SIZE = 660;
    int forcedIdx;
    int targetIdx;
    GLRenderer outer;

    public TorqueImpulseStrategy(int forcedIdx, int targetIdx, GLRenderer outer) {
        this.outer = outer;
        this.forcedIdx = forcedIdx;
        this.targetIdx = targetIdx;
    }

    public void applyStrategy() {
        Transform t = new Transform();
        Vector3f forcedOrigin;
        Vector3f targetOrigin; // inputs
        // inputs
        Vector3f forcedTargetVector;
        // inputs
        Vector3f rightVector;
        // inputs
        Vector3f randomness; // middle steps
        // inputs
        // middle steps
        Vector3f applyTorque; // results
        outer.rigidBodies[forcedIdx].getWorldTransform(t);
        forcedOrigin = new Vector3f(t.origin);
        outer.rigidBodies[targetIdx].getWorldTransform(t);
        targetOrigin = new Vector3f(t.origin);
        forcedTargetVector = new Vector3f(forcedOrigin);
        forcedTargetVector.negate();
        forcedTargetVector.add(targetOrigin);
        forcedTargetVector.normalize();
        rightVector = new Vector3f(0.0F, 1.0F, 0.0F);
        rightVector.cross(rightVector, forcedTargetVector);
        randomness = new Vector3f(random(), random(), random());
        //Log.log(this.getClass().getName() + "(" + forcedIdx + ", " + targetIdx + ")");
        //Log.log(this.getClass().getName() + ".Forced    Origin = " + forcedOrigin.toString());
        //Log.log(this.getClass().getName() + ".Target    Origin = " + targetOrigin.toString());
        //Log.log(this.getClass().getName() + ".Random Direction = " + randomness.toString());
        //Log.log(this.getClass().getName() + ".T-F    Direction = " + forcedTargetVector.toString());
        //Log.log(this.getClass().getName() + ".r(T-F) Direction = " + rightVector.toString());
        applyTorque = new Vector3f(rightVector);
        applyTorque.add(randomness);
        applyTorque.normalize();
        applyTorque.scale(IMPULSE_SIZE);
        Log.log(this.getClass().getName() + " => forced applyImpulse: Torque(" + applyTorque.toString() + ")");
        outer.rigidBodies[forcedIdx].applyTorqueImpulse(applyTorque);
    }

    protected float random() {
        return (float) (Math.random() * RANDOM_SIZE + RANDOM_BASE);
    }
}
