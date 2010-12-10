/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class SceneCamera implements ITransformAttribute {
    private float px, py, pz, rx, ry, rz, r;
    
    public void transform(GL gl) {
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glTranslatef(-px, -py, -pz);
        gl.glRotatef(rx, ry, rz, -r);
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

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
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
