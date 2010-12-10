package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 * Class representing the main anchor, or root of the scene graph.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class SceneGraph {
    private ISceneNode root = new GenericSceneNode(new ISceneAttribute[] {});
    private SceneCamera camera;

    public void frame(GL gl, float td) {
        if(camera != null)
            camera.transform(gl);

        root.update(td);
        root.render(gl);

        if(camera != null)
            camera.transformEnd(gl);
    }

    public void addChild(ISceneNode node) {
        root.addChild(node);
    }

    public SceneCamera getCamera() {
        return camera;
    }

    public void setCamera(SceneCamera camera) {
        this.camera = camera;
    }
}
