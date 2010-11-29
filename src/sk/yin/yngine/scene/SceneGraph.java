package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class SceneGraph {
    private SceneGroup rootNode = new SceneGroup();
    private SceneCamera camera;

    public void frame(GL gl, float td) {
        if(camera != null)
            camera.transform(gl);

        rootNode.update(td);
        rootNode.render(gl);

        if(camera != null)
            camera.transformEnd(gl);
    }

    public void addChild(ISceneNode sceneNode) {
        rootNode.addChild(sceneNode);
    }

    public SceneCamera getCamera() {
        return camera;
    }

    public void setCamera(SceneCamera camera) {
        this.camera = camera;
    }
}
