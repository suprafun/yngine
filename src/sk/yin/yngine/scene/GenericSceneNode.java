package sk.yin.yngine.scene;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2; 

import sk.yin.yngine.scene.attributes.IGeometryAttribute;
import sk.yin.yngine.scene.attributes.ISceneAttribute;
import sk.yin.yngine.scene.attributes.ISceneAttribute.RenderStage;
import sk.yin.yngine.scene.attributes.ITransformAttribute;

/**
 * A node in scene graph, which can have multiple attributes specified. It will
 * accept any child.
 *
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class GenericSceneNode implements ISceneNode {
    private List<ISceneNode> children = new ArrayList<ISceneNode>();
    private IGeometryAttribute geometry;
    private ITransformAttribute transform;
    private ISceneAttribute attributes[];

    public GenericSceneNode(ISceneAttribute... attributes) {
        this.attributes = attributes;
        geometry = this.<IGeometryAttribute>find(IGeometryAttribute.class);
        transform = this.<ITransformAttribute>find(ITransformAttribute.class);
    }

    public void render(GL2  gl) {
        for(RenderStage stage : RenderStage.values()) {
            for (ISceneAttribute attr : attributes) {
                attr.render(gl, stage);
            }
            if (stage == RenderStage.RENDER) {
                for(ISceneNode child : children)
                    child.render(gl);
            }
        }
    }

    public void update(float deltaTime) {
        for(ISceneAttribute attr : attributes)
            attr.update(deltaTime);
        for(ISceneNode child : children)
            child.update(deltaTime);
    }

    // TODO(mgagyi): What to do with them? Call listeners?
    public void onAdded(SceneGraph graph, ISceneAttribute parent) {
    }

    public void onRemoved(SceneGraph graph) {
    }

    /**
     * Finds a property of class <code>type</code> in properties list supplied
     * to the Constructor. This method checks for known classes
     * (e.g. IGeometryAttribute and ITransformAttribute).
     *
     * @param type Class or interface of sought for node property.
     * @return Node property object, or null if not found.
     */
     // NOTE(yinotaurus): Target for start-up performance enhancements.
    protected <X extends ISceneAttribute> X find(Class<X> type) {
        if(type == null)
            return null;
        if(geometry != null && IGeometryAttribute.class.equals(type))
            return (X) geometry;
        if(transform != null && ITransformAttribute.class.equals(type))
            return (X) transform;
        for(ISceneAttribute property : attributes) {
            if(type.isInstance(property))
                return (X) property;
        }
        return null;
    }

    public <X extends ISceneAttribute> X attribute(Class<X> type) {
        return this.<X>find(type);
    }

    public <X extends ISceneAttribute> X attribute(Class<X> type, String query) {
        if(query == null || query.isEmpty())
            return this.<X>find(type);
        else
            return null;
    }

    public boolean addChild(ISceneNode sceneNode) {
        children.add(sceneNode);
        return true;
    }
}
