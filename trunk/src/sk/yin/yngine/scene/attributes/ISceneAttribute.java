package sk.yin.yngine.scene.attributes;

import javax.media.opengl.GL2;

/**
 * Attribute of a node in scene graph. One node can be made by many attributes.
 * E.g. Geometry and physics/static-transformation attributes; Trigger
 * attribute.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public interface ISceneAttribute {
    // TODO(yin): Change the RenderStage enumeration to something more flexible.
    enum RenderStage {
        PRERENDER,      // Before rendering self and childs (transforms, etc.)
        RENDER,         // Render yourself
        RENDER_NORMALS, // Render your normals
        // TODO(mgagyi): RENDER_TRANSLUCENT stage, but this can change
        POSTRENDER,     // After rendering self and childs (pop matrix, etc.)
    }
    
    public void update(float deltaTime);

    /**
     * If interested in rendering stage, executes some actions.
     * @param gl
     * @param stage Rendering stage.
     */
    public void render(GL2 gl, RenderStage stage);
}
