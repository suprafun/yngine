package sk.yin.yngine.scene;

import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import javax.media.opengl.GL;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * Scene transformation updated from JBullet physics engine. Itself extends
 * JBullet DefaultMotionState and is directly passed into JBullet. This node's
 * events and query methods return always null.
 * 
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class PhysicsAttribute extends DefaultMotionState implements ITransformAttribute {
    private ITransformAttribute transform;

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

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void render(GL gl, RenderStage stage) {
        switch(stage) {
            case PRERENDER:
                transform(gl);
                break;
            case POSTRENDER:
                transformEnd(gl);
                break;
        }
    }

    public void transform(GL gl) {
        if (transform != null) {
            Vector3f origin = transform.origin();
            Matrix3f basis = transform.basis();
            gl.glPushMatrix();

            if (origin != null) {
                gl.glTranslatef(origin.x, origin.y, origin.z);
            }
            if (basis != null) {
                gl.glMultMatrixf(new float[]{
                            basis.m00, basis.m01, basis.m02, 0f,
                            basis.m10, basis.m11, basis.m12, 0f,
                            basis.m20, basis.m21, basis.m22, 0f,
                            0f, 0f, 0f, 1f
                        }, 0);
            }
        }
    }

    public void transformEnd(GL gl) {
        gl.glPopMatrix();
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
