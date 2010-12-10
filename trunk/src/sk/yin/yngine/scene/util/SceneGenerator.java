package sk.yin.yngine.scene.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import sk.yin.yngine.scene.ISceneAttribute;
import sk.yin.yngine.scene.ISceneNode;
import sk.yin.yngine.scene.SceneGraph;

/**
 * Used to generate different parts of scene graph.
 *
 * @since Date: 10.12.10
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class SceneGenerator {
    private SceneGraph graph = null;
    private Class<ISceneNode> nodeType = null;
    private List<ISceneAttribute> attributes = new ArrayList<ISceneAttribute>();

    public SceneGenerator() {
    }

    public SceneGenerator root() {
        graph = new SceneGraph();
        nodeType = null;
        attributes.clear();
        return this;
    }

    public SceneGenerator node(Class<ISceneNode> type) {
        if(nodeType != null) {
            graph.addChild(createNode(nodeType));
            attributes.clear();
        }
        nodeType = type;
        return this;
    }

    public SceneGenerator attr(Class<ISceneAttribute> type) {
        try {
            Constructor<ISceneAttribute> constructor = type.getConstructor();
            attributes.add(constructor.newInstance());
        } catch(NoSuchMethodException ex) {
            // Skip
        } catch(InstantiationException ex) {
            // Skip
        } catch(IllegalAccessException ex) {
            // Skip
        } catch(InvocationTargetException ex) {
            // Skip
        }
        return this;
    }

    protected ISceneNode createNode(Class<ISceneNode> type) {
        ISceneNode node = null;
        try {
            Constructor<ISceneNode> constructor;
            ISceneAttribute[] attrs = (ISceneAttribute[]) attributes.toArray();
            constructor = type.getConstructor(attributes.getClass());
            node = constructor.newInstance((Object) attributes);
        } catch(NoSuchMethodException ex) {
            // Skip
        } catch(InstantiationException ex) {
            // Skip
        } catch(IllegalAccessException ex) {
            // Skip
        } catch(InvocationTargetException ex) {
            // Skip
        }
        return node;
    }
}
