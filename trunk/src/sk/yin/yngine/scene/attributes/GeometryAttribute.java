package sk.yin.yngine.scene.attributes;

import javax.media.opengl.GL;
import sk.yin.yngine.geometry.Model;

/**
 * Scene graph attribute for renderable geometry - a mesh Model.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class GeometryAttribute implements IGeometryAttribute {
    private Model model;

    public GeometryAttribute(Model model) {
        this.model = model;
    }

    public void update(float deltaTime) {
        // There is nothing to update
    }

    public void render(GL gl, RenderStage stage) {
        if(model != null && stage == RenderStage.RENDER) {
            model.render(gl);
        } else if (false && model != null && stage == RenderStage.RENDER_NORMALS) {
            // TODO(yin): Separate scenegraph, models and rederer.
            model.renderNormals(gl);
        }
    }

}
