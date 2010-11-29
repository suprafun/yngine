/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.yngine.scene;

import javax.media.opengl.GL;
import sk.yin.yngine.math.Model;

/**
 *
 * @author yin
 */
public class SceneObject implements ISceneNode, ISceneTransformation {
    private Model model;
    private float px, py, pz, rx, ry, rz;

    public SceneObject(Model model) {
        this.model = model;
    }

    public void render(GL gl) {
        transform(gl);
        model.render(gl);
        transformEnd(gl);
    }

    public void update(float deltaTime) {
        return;
    }

    public void transform(GL gl) {
        gl.glPushMatrix();
        if(px!=0 || py!=0 || pz!=0)
            gl.glTranslatef(px, py, pz);
        if(rx!=0 || ry!=0 || rz!=0) {
            gl.glRotatef(rx, 1.0f, 0.0f, 0.0f);
            gl.glRotatef(ry, 0.0f, 1.0f, 0.0f);
            gl.glRotatef(rz, 0.0f, 0.0f, 1.0f);
        }
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
