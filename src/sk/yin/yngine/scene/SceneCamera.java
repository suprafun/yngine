package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 * Simple SceneCamera implementation.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class SceneCamera implements ISceneAttribute {
    private float px, py, pz, rx, ry, rz;

    public void update(float deltaTime) {
        // Do nothing.
    }

    public void render(GL gl, RenderStage stage) {
        // TODO(yin): What about adding a SETUP and SHUTDOWN rendering stages?
    }
    
    public void transform(GL gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(-px, -py, -pz);
        gl.glRotatef(rx, ry, rz, -1f);
    }

    public void transformEnd(GL gl) {
        gl.glPopMatrix();
    }


    public float getPx() {
        return px;
    }

    public void setPx(float px) {
        this.px = px;
    }

    public float getPy() {
        return py;
    }

    public void setPy(float py) {
        this.py = py;
    }

    public float getPz() {
        return pz;
    }

    public void setPz(float pz) {
        this.pz = pz;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }

    public float getRz() {
        return rz;
    }

    public void setRz(float rz) {
        this.rz = rz;
    }
}
