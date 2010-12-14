package sk.yin.yngine.scene.util;

import javax.vecmath.TexCoord2f;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.math.Triple;
import sk.yin.yngine.scene.util.ModelBuilder.Decorator;

/**
 * Base class for common decorators.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public abstract class BaseDecorator implements Decorator {
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
}
