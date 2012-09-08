package sk.yin.yngine.scene;

import javax.media.opengl.GL2;

/**
 * Defines a light node.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public interface ILightNode extends ISceneNode {
    public void turnOn(GL2 gl, int glLight);
    public void turnOff(GL2 gl, int glLight);
}
