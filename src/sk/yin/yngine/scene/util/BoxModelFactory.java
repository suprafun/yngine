package sk.yin.yngine.scene.util;

import sk.yin.yngine.math.Model;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.math.Triangle;

/**
 *
 * @author yin
 */
public class BoxModelFactory {
    private static BoxModelFactory instance;

    private static final float cubeVertices[] = {
        -1.0f, -1.0f, -1.0f,
         1.0f, -1.0f, -1.0f,
        -1.0f,  1.0f, -1.0f,
         1.0f,  1.0f, -1.0f,
        -1.0f, -1.0f,  1.0f,
         1.0f, -1.0f,  1.0f,
        -1.0f,  1.0f,  1.0f,
         1.0f,  1.0f,  1.0f
    };
    private static final int cubeFaces[] = {
        0, 1, 2,    1, 3, 2,    // Front
        1, 5, 3,    5, 7, 3,    // Right
        3, 7, 2,    7, 2, 6,    // Bottom
        7, 6, 5,    6, 4, 5,    // Back
        4, 0, 6,    0, 2, 6,    // Left
        4, 0, 5,    0, 1, 5     // Top
    };
    private static final float cubeNormals[] = {
        0.0f,  0.0f, -1.0f,
        0.0f,  0.0f, -1.0f,
        1.0f,  0.0f,  0.0f,
        1.0f,  0.0f,  0.0f,
        0.0f,  1.0f,  0.0f,
        0.0f,  1.0f,  0.0f,
        0.0f,  0.0f,  1.0f,
        0.0f,  0.0f,  1.0f,
       -1.0f,  0.0f,  0.0f,
       -1.0f,  0.0f,  0.0f,
        0.0f, -1.0f,  0.0f,
        0.0f, -1.0f,  0.0f,
    };

    private BoxModelFactory() {
    }

    public static BoxModelFactory getInstance() {
        if (instance == null)
            instance = new BoxModelFactory();
        return instance;
    }

    public Model createBox(float x, float y, float z) {
        Point3f scale = new Point3f();
        ModelBuilder mb = new ModelBuilder();
        for(int i = 0, l = cubeVertices.length; i < l; i += 3) {
            Point3f vertex = new Point3f(cubeVertices[i], cubeVertices[i+1], cubeVertices[i+2]);
            vertex.multiply(scale);
            mb.addVertex(vertex);
            mb.addColor(new Point3f((float)Math.random(), (float)Math.random(), (float)Math.random()));

        }
        for(int i = 0, l = cubeFaces.length; i < l; i += 3) {
            Triangle triangle = new Triangle(cubeFaces[i], cubeFaces[i+1], cubeFaces[i+2]);
            Point3f normal = new Point3f(cubeNormals[i], cubeNormals[i+1], cubeNormals[i+2]);
            mb.addFace(triangle);
            mb.addNormal(normal);
        }
        return mb.toModel(Model.ModelLayout.VerticleBound, Model.ModelLayout.PerFace);
    }
}
