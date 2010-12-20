package sk.yin.yngine.scene.decorators;

import sk.yin.yngine.scene.decorators.BaseDecorator;
import java.util.HashMap;
import java.util.Map;
import javax.vecmath.Matrix3f;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.math.Triple;

/**
 * Generates color by using vertex normals.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class NormalBasedColorDecorator extends BaseDecorator {
    private Map<Integer, Integer> map =
            new HashMap<Integer, Integer>();
    // TODO(yin): Finish rotations; Point3f, Vector3f conflict;
    private Matrix3f rotation;

    public NormalBasedColorDecorator() {
        rotation = new Matrix3f();
        rotation.setIdentity();
    }

    public NormalBasedColorDecorator(Matrix3f rotation) {
        this.rotation = rotation;
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
            int i = builder.addColor(new Point3f(
                    Math.abs(normal.x),
                    Math.abs(normal.y),
                    Math.abs(normal.z)));
            map.put(idx, i);
        }
    }

    @Override
    public void onNormalTriple(Triple normals) {
        if (builder != null) {
            Triple colors = new Triple(map.get(normals.idx1),
                    map.get(normals.idx2),
                    map.get(normals.idx3));
            builder.appendColorIndexes(colors);
        }
    }
}
