package sk.yin.yngine.scene.decorators;

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
public class NormalBasedTextureDecorator extends BaseDecorator {
    private Map<Integer, Integer> map =
            new HashMap<Integer, Integer>();
    // TODO(yin): Finish rotations; Point3f, Vector3f conflict;
    private Matrix3f rotation;

    public NormalBasedTextureDecorator() {
        rotation = new Matrix3f();
        rotation.setIdentity();
    }

    public NormalBasedTextureDecorator(Matrix3f rotation) {
        this.rotation = rotation;
    }

    /**
     * Computes D - the texture coordinate correction coefficient.
     * @param normalZ Z coordinate of normal
     * @return Displacement coefficient of texture coordinate.
     */
    protected float getTextureCorrection(float normalZ) {
        return 1.0f / (float) (Math.sin(Math.abs(normalZ)) + 1.0f);
    }

    public Matrix3f getRotation() {
        return rotation;
    }

    public void setRotation(Matrix3f rotation) {
        this.rotation = rotation;
    }

    @Override
    public void onNewNormal(int idx, Point3f normal) {
        if (builder != null) {
            Point3f nn = normal.copy().normalize();
            float d = getTextureCorrection(nn.z);
            TexCoord2f texCoord = new TexCoord2f(nn.x * d, nn.y * d);
            int i = builder.addTexCoord(texCoord);
            map.put(idx, i);
        }
    }

    @Override
    public void onNormalTriple(Triple normals) {
        if (builder != null) {
            Triple texCoords = new Triple(map.get(normals.idx1),
                    map.get(normals.idx2),
                    map.get(normals.idx3));
            builder.appendTexCoordIndexes(texCoords);
        }
    }
}
