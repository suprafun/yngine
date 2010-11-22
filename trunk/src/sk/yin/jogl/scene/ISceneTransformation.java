/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.jogl.scene;

import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public interface ISceneTransformation {
    public void transform(GL gl);
    public void transformEnd(GL gl);

    public float getPx();
    public void setPx(float px);
    public float getPy();
    public void setPy(float py);
    public float getPz();
    public void setPz(float pz) ;
    public float getR();
    public void setR(float r);
    public float getRx();
    public void setRx(float rx);
    public float getRy();
    public void setRy(float ry);
    public float getRz();
    public void setRz(float rz);
}
