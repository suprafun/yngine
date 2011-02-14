package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 * Defines a light node.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public interface ILightNode extends ISceneNode {
    public void turnOn(GL gl, int glLight);
    public void turnOff(GL gl, int glLight);
}
