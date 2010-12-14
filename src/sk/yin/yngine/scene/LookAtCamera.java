package sk.yin.yngine.scene;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;
import javax.vecmath.Vector3f;
import sk.yin.yngine.util.Log;

/**
 * Simple LookAtCamera implementation.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class LookAtCamera implements ISceneAttribute {
    protected Vector3f position = new Vector3f(0f, 0f, 40f),
            target = new Vector3f(0f, 0f, 0f),
            up = new Vector3f(0f, 1f, 0f);

    public void update(float deltaTime) {
        // Do nothing.
    }

    public void render(GL gl, RenderStage stage) {
        // TODO(yin): What about adding a SETUP and SHUTDOWN rendering stages?
    }
    
    public void transform(GL gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        GLU glu = new GLU();
        glu.gluLookAt(position.x, position.y, position.z,
                target.x, target.y, target.z,
                up.x, up.y, up.z);
        Log.log(position.x+","+ position.y+","+ position.z + "\t"
                + target.x+","+ target.y+","+ target.z + "\t"
                + up.x+","+ up.y+","+ up.z);
    }

    public void transformEnd(GL gl) {
        gl.glPopMatrix();
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        if(position != null)
            this.position = position;
    }

    public Vector3f getTarget() {
        return target;
    }

    public void setTarget(Vector3f target) {
        if(position != null)
            this.target = target;
    }

    public Vector3f getUp() {
        return up;
    }

    public void setUp(Vector3f up) {
        if(position != null)
            this.up = up;
    }
}
