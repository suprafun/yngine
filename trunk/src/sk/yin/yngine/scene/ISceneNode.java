package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 * Generic node of the scene graph.
 *
 * @author Matej 'Yin' Gagyi
 */
public interface ISceneNode {
    void render(GL gl);
    void update(float deltaTime);
}
