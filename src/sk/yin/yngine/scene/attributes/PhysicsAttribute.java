package sk.yin.yngine.scene.attributes;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

/**
 * Scene transformation updated from JBullet physics engine. Itself extends
 * JBullet DefaultMotionState and is directly passed into JBullet. This node's
 * events and query methods return always null.
 * 
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
// TODO(yin): This attribute should be created together with JBullet objects.
public class PhysicsAttribute extends DefaultMotionState
        implements ITransformAttribute {
    /**
     * Creates a new DefaultMotionState with all transforms set to identity.
     */
    public PhysicsAttribute() {
        super();
    }

    /**
     * Creates a new DefaultMotionState with initial world transform and center
     * of mass offset transform set to identity.
     */
    public PhysicsAttribute(Transform startTrans) {
        super(startTrans);
    }

    /**
     * Creates a new DefaultMotionState with initial world transform and center
     * of mass offset transform.
     */
    public PhysicsAttribute(Transform startTrans, Transform centerOfMassOffset) {
        super(startTrans, centerOfMassOffset);
    }

    public PhysicsAttribute(Vector3f origin) {
        super();
        startWorldTrans.origin.set(origin);
        graphicsWorldTrans.origin.set(origin);
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render(GL2 gl, RenderStage stage) {
        switch(stage) {
            case PRERENDER:
                transform(gl);
                break;
            case POSTRENDER:
                transformEnd(gl);
                break;
        }
    }

    public void transform(GL2 gl) {
        if (graphicsWorldTrans != null) {
            Vector3f origin = graphicsWorldTrans.origin;
            Matrix3f basis = graphicsWorldTrans.basis;
            gl.glPushMatrix();

            if (origin != null) {
                gl.glTranslatef(origin.x, origin.y, origin.z);
            }
            if (basis != null) {
                gl.glMultMatrixf(new float[]{
                            basis.m00, basis.m10, basis.m20, 0f,
                            basis.m01, basis.m11, basis.m21, 0f,
                            basis.m02, basis.m12, basis.m22, 0f,
                            0f, 0f, 0f, 1f
                        }, 0);
            }
        }
    }

    public void transformEnd(GL2 gl) {
        gl.glPopMatrix();
    }

    /**
     * Resets the position in world the the original at simulation setup.
     * This method retains rotation without changing it.
     */
    public void resetToStartOrigin(CollisionObject body) {
        body.setWorldTransform(startWorldTrans);
    }

    @Override
    public Vector3f origin() {
        return graphicsWorldTrans.origin;
    }

    @Override
    public void origin(Vector3f origin) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Matrix3f basis() {
        return graphicsWorldTrans.basis;
    }

    @Override
    public void basis(Matrix3f basis) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
