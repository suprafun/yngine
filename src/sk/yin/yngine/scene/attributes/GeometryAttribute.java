package sk.yin.yngine.scene.attributes;

import javax.media.opengl.GL2;

import sk.yin.yngine.geometry.Model;
import sk.yin.yngine.render.lights.MaterialDef;

/**
 * Scene graph attribute for renderable geometry - a mesh Model.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class GeometryAttribute implements IGeometryAttribute {
    private Model model;
    private MaterialDef surface;

    public GeometryAttribute(Model model, MaterialDef surface) {
        this.model = model;
        this.surface = surface;
    }

    public void update(float deltaTime) {
        // There is nothing to update
    }

    public void render(GL2 gl, RenderStage stage) {
        if(model != null && stage == RenderStage.RENDER) {
            if(surface != null) {
                surface.use(gl);
            } else {
                MaterialDef.Half.use(gl);
            }
            model.render(gl);
        } else if (false && model != null && stage == RenderStage.RENDER_NORMALS) {
            // TODO(yin): Separate scenegraph, models and rederer.
            model.renderNormals(gl);
        }
    }

}
