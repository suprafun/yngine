/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.jogl.scene;

import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class SceneGroup implements ISceneNode {
    private List<ISceneNode> children = new ArrayList<ISceneNode>();

    public void update(float deltaTime) {
        for(ISceneNode node: children) {
            node.update(deltaTime);
        }
    }

    public void render(GL gl) {
        for(ISceneNode node: children) {
            node.render(gl);
        }
    }

    void addChild(ISceneNode sceneNode) {
        children.add(sceneNode);
    }
}
