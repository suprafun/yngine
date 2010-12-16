package sk.yin.yngine.scene.camera;

import javax.media.opengl.GL;
import javax.vecmath.Vector3f;

/**
 * Smooths changes to transformation of another underlaying camera object.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class SmoothingCameraProxy extends LookAtCamera {
    private LookAtCamera camera;

    public SmoothingCameraProxy(LookAtCamera camera) {
        this.camera = camera;
        position = camera.getPosition();
        target = camera.getTarget();
        up = camera.getUp();
    }

    @Override
    public void update(float deltaTime) {
        camera.setPosition(c(camera.getPosition(), position, deltaTime));
        camera.setTarget(c(camera.getTarget(), target, deltaTime));
        //camera.setUp(c(camera.getTarget(), finalTarget, deltaTime));
    }

    protected Vector3f c(Vector3f current, Vector3f target, float deltaTime) {
        if (current.equals(target)) {
            return current;
        }

        Vector3f ret = new Vector3f(target);
        ret.sub(current);
        ret.scale(deltaTime);
        ret.add(current);
        return ret;
    }

    @Override
    public void transform(GL gl) {
        camera.transform(gl);
    }

    @Override
    public void transformEnd(GL gl) {
        camera.transformEnd(gl);
    }
}
