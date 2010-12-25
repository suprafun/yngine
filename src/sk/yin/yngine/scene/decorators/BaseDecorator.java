package sk.yin.yngine.scene.decorators;

import javax.vecmath.TexCoord2f;
import javax.vecmath.Vector3f;
import sk.yin.yngine.geometry.Point3f;
import sk.yin.yngine.geometry.Triple;
import sk.yin.yngine.scene.generators.ModelBuilder;
import sk.yin.yngine.scene.generators.ModelBuilder.Decorator;

/**
 * Base class for common decorators.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public  class BaseDecorator implements Decorator {
    protected ModelBuilder builder;

    public void setModelBuilder(ModelBuilder builder) {
        this.builder = builder;
    }

    public void onNewVertex(int idx, Point3f vertex) {
    }

    public void onKnownVertex(int idx, Point3f vertex) {
    }

    public void onNewNormal(int idx, Point3f normal) {
    }

    public void onKnownNormal(int idx, Point3f normal) {
    }

    public void onNewColor(int idx, Point3f color) {
    }

    public void onKnownColor(int idx, Point3f color) {
    }

    public void onNewTexCoord(int idx, TexCoord2f texCoord) {
    }

    public void onKnownTexCoord(int idx, TexCoord2f texCoord) {
    }

    public void onNewFace(int idx, Triple face) {
    }

    public void onKnownFace(int idx, Triple face) {
    }

    public void onNormalTriple(Triple normals) {
    }

    public void onBegin(Vector3f boundingBox) {
    }

    public void onEnd() {
    }

    protected Point3f normalize(Point3f point, Vector3f boundingBox) {
        Point3f bb = new Point3f(boundingBox);
        Point3f norm = point.copy()
                    .divide(bb).add(1f).multiply(0.5f);
        return norm;
    }
}
