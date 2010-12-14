package sk.yin.yngine.scene.util;

import javax.vecmath.TexCoord2f;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.math.Triple;
import sk.yin.yngine.scene.util.ModelBuilder.Decorator;

/**
 * Generates texture coordinates by using vertex normals.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class NormalTextureFrontDecorator implements Decorator {
    private ModelBuilder builder;

    public void setModelBuilder(ModelBuilder builder) {
        this.builder = builder;
    }

    public void onNewVertex(int idx, Point3f vertex) {
    }

    public void onKnownVertex(int idx, Point3f vertex) {
    }

    public void onNewNormal(int idx, Point3f normal) {
        if(builder != null) {
            Point3f nn = normal.copy().normalize();
            float d = getTextureCorrection(nn.z);
            TexCoord2f texCoord = new TexCoord2f(nn.x * d, nn.y * d);
            builder.addTexCoord(texCoord);
        }
    }

    public void onKnownNormal(int idx, Point3f normal) {
    }

    public void onNewFace(int idx, Triple face) {
        if (builder != null) {
            builder.appendTexCoordIndexes(face);
        }
    }

    public void onKnownFace(int idx, Triple face) {
    }


    /**
     * Computes D - the texture coordinate correction coefficient.
     * @param normalZ Z coordinate of normal
     * @return Displacement coefficient of texture coordinate.
     */
    protected float getTextureCorrection(float normalZ) {
        return 1.0f / (float) (Math.sin(Math.abs(normalZ)) + 1.0f);
    }
}
