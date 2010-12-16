package sk.yin.yngine.scene.attributes;

import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

/**
 * Scene transformation node.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public interface ITransformAttribute extends ISceneAttribute {
    public Vector3f origin();
    public void origin(Vector3f origin);
    public Matrix3f basis();
    public void basis(Matrix3f basis);
}
