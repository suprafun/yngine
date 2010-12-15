package sk.yin.yngine.scene.util;

import javax.vecmath.Vector3f;
import sk.yin.yngine.math.Model;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.math.Triple;

/**
 * Creates models of boxes with different sizes.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class BoxModelGenerator {
    private static BoxModelGenerator instance;

    private static final float cubeVertices[] = {
        -1.0f,  1.0f, -1.0f,
         1.0f,  1.0f, -1.0f,
        -1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f,
        -1.0f,  1.0f,  1.0f,
         1.0f,  1.0f,  1.0f,
        -1.0f, -1.0f,  1.0f,
         1.0f, -1.0f,  1.0f
    };
    private static final int cubeFaces[] = {
        0, 1, 2,    1, 3, 2,    // Front
        1, 5, 3,    5, 7, 3,    // Right
        3, 7, 2,    7, 2, 6,    // Bottom
        7, 6, 5,    6, 4, 5,    // Back
        4, 0, 6,    0, 2, 6,    // Left
        4, 0, 5,    0, 1, 5     // Top
    };
    // TODO(yin): Normals doesn't work right for boxes.
    private static final float cubeNormals[] = {
        0.0f,  0.0f, -1.0f,
        0.0f,  0.0f, -1.0f,
        1.0f,  0.0f,  0.0f,
        1.0f,  0.0f,  0.0f,
        0.0f, -1.0f,  0.0f,
        0.0f, -1.0f,  0.0f,
        0.0f,  0.0f,  1.0f,
        0.0f,  0.0f,  1.0f,
       -1.0f,  0.0f,  0.0f,
       -1.0f,  0.0f,  0.0f,
        0.0f,  1.0f,  0.0f,
        0.0f,  1.0f,  0.0f,
    };

    private BoxModelGenerator() {
    }

    public static BoxModelGenerator instance() {
        if (instance == null)
            instance = new BoxModelGenerator();
        return instance;
    }

    public Model createBox(ModelBuilder builder, float x, float y, float z) {
        Point3f scale = new Point3f(x, y, z);
        builder.begin(new Vector3f(x, y, z));
        for(int i = 0, l = cubeVertices.length; i < l; i += 3) {
            Point3f vertex = new Point3f(cubeVertices[i], cubeVertices[i+1], cubeVertices[i+2]);
            vertex.multiply(scale);
            builder.addVertex(vertex);
        }
        for(int i = 0, l = cubeFaces.length; i < l; i += 3) {
            Triple triangle = new Triple(cubeFaces[i], cubeFaces[i+1], cubeFaces[i+2]);
            Point3f normal = new Point3f(cubeNormals[i], cubeNormals[i+1], cubeNormals[i+2]);
            builder.addFace(triangle);
            int idx = builder.addNormal(normal);
            builder.appendNormalIndexes(new Triple(idx));
        }
        return builder.end();
    }
}
