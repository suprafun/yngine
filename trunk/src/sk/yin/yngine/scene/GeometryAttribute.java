package sk.yin.yngine.scene;

import javax.media.opengl.GL;
import sk.yin.yngine.math.Model;

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
        }
    }

}
