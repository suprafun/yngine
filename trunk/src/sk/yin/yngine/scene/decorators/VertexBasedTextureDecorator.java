package sk.yin.yngine.scene.decorators;

import sk.yin.yngine.scene.decorators.BaseDecorator;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Matrix3f;
import javax.vecmath.TexCoord2f;
import sk.yin.yngine.geometry.Point3f;
import sk.yin.yngine.geometry.Triple;

/**
 * Generates texture coordinates by using vertex normals.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class VertexBasedTextureDecorator extends BaseDecorator {
    private Map<Integer, Integer> map =
            new HashMap<Integer, Integer>();
    // TODO(yin): Finish rotations; Point3f, Vector3f conflict;
    private Matrix3f rotation;
    private float scaleX;
    private float scaleY;

    public VertexBasedTextureDecorator() {
        rotation = new Matrix3f();
        rotation.setIdentity();
        scaleX = scaleY = 1.0f;
    }

    public VertexBasedTextureDecorator(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }

    public VertexBasedTextureDecorator(Matrix3f rotation) {
        this.rotation = rotation;
    }

    public Matrix3f getRotation() {
        return rotation;
    }

    public void setRotation(Matrix3f rotation) {
        this.rotation = rotation;
    }

    @Override
    public void onNewVertex(int idx, Point3f vertex) {
        if (builder != null) {
            Point3f nv = normalize(vertex, builder.boundingBox());
            TexCoord2f texCoord = new TexCoord2f(
                    (nv.x + nv.z) * scaleX,
                    (nv.y + nv.z) * scaleY);
            int i = builder.addTexCoord(texCoord);
            map.put(idx, i);
        }
    }

    @Override
    public void onNewFace(int idx, Triple face) {
        if (builder != null) {
            Triple texCoords = new Triple(map.get(face.idx1),
                    map.get(face.idx2),
                    map.get(face.idx3));
            builder.appendTexCoordIndexes(texCoords);
        }
    }
}
