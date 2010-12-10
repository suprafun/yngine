package sk.yin.yngine.scene;

import javax.media.opengl.GL;

/**
 * Scene node in scene graph. Every node has attributes and children. When
 * traversing the graph, every node invokes its children with different
 * rendering step indicator as parameter.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public interface ISceneNode {
    public void update(float deltaTime);
    public void render(GL gl);

    // TODO(mgagyi): Move event handling to INodeListens (... or something)
    public void onAdded(SceneGraph graph, ISceneAttribute parent);
    public void onRemoved(SceneGraph graph);

    /**
     * Queries node attribute, which is of type <code>X</code>.
     * @param type Attribute type
     * @return Matching attribute
     */
    public <X extends ISceneAttribute> X attribute(Class<X> type);

    /**
     * Queries node attribute, which is of type <code>X</code> and matches
     * <code>query</code>. For example, a name can be used as the query.
     * @param type Attribute type
     * @param query
     * @return Matching attribute
     */
    public <X extends ISceneAttribute> X attribute(Class<X> type, String query);

    /**
     * Adds a child and returns true, otherwise returns false
     * @param child Child node to be added.
     * @return True if child was not rejected
     */
    public boolean addChild(ISceneNode child);
}
