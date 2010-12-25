package sk.yin.yngine.scene.decorators;

import javax.vecmath.Vector3f;
import sk.yin.yngine.geometry.Point3f;
import sk.yin.yngine.geometry.Triple;

/**
 * Generates color by using vertex normals.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class StaticColorDecorator extends BaseDecorator {
    private int colorIdx = -1;
    private float r, g, b;

    public StaticColorDecorator() {
    }

    public StaticColorDecorator(float r, float g, float b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    @Override
    public void onBegin(Vector3f boundingBox) {
        if (builder != null) {
            colorIdx = builder.addColor(new Point3f(r, g, b));
        }
    }

    @Override
    public void onNewFace(int idx, Triple face) {
        if (builder != null) {
            builder.appendColorIndexes(new Triple(colorIdx, colorIdx, colorIdx));
        }
    }

}
