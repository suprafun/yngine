package sk.yin.yngine.scene.util;

import sk.yin.yngine.math.Model;
import sk.yin.yngine.math.Triangle;
import sk.yin.yngine.math.Point3f;
import java.util.ArrayList;

import java.util.List;

/**
 * Creates mesh model of a sphere interpolated from a base mesh
 * (e.g. Octahedron).
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class SphereModelGenerator {
    private static SphereModelGenerator instance;
    private static final float octahedronVertices[] = {
        0.0f, -1.0f, 0.0f,
        1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f,
        -1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, -1.0f,
        0.0f, 1.0f, 0.0f
    };
    private static final int octahedronFaces[] = {
        0, 1, 2,
        0, 2, 3,
        0, 3, 4,
        0, 4, 1,
        1, 5, 2,
        2, 5, 3,
        3, 5, 4,
        4, 5, 1
    };
    private static final float tetrahedronVertices[] = {
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, 1.0f
    };
    private static final int tetrahedronFaces[] = {
        0, 1, 2,
        1, 0, 3,
        2, 0, 3,
        3, 2, 1
    };
    private static final float cubeVertices[] = {
        -1.0f, -1.0f, -1.0f,
        1.0f, -1.0f, -1.0f,
        -1.0f, 1.0f, -1.0f,
        1.0f, 1.0f, -1.0f,
        -1.0f, -1.0f, 1.0f,
        1.0f, -1.0f, 1.0f,
        -1.0f, 1.0f, 1.0f,
        1.0f, 1.0f, 1.0f
    };
    private static final int cubeFaces[] = {
        0, 1, 2, 1, 3, 2, // Front
        1, 5, 3, 5, 7, 3, // Right
        3, 7, 2, 7, 2, 6, // Bottom
        7, 6, 5, 6, 4, 5, // Back
        4, 0, 6, 0, 2, 6, // Left
        4, 0, 5, 0, 1, 5 // Top
    };

    public enum BasePolyhedron {
        TETRAHEDRON, OCTAHEDRON, CUBE
    };

    private SphereModelGenerator() {
    }

    public static SphereModelGenerator instance() {
        if (instance == null) {
            instance = new SphereModelGenerator();
        }
        return instance;
    }

    public Model createSphere(float r, int iter) {
        return createSphere(r, iter, BasePolyhedron.TETRAHEDRON);
    }

    public Model createSphere(float r, int iter, BasePolyhedron base) {
        float[] verts;
        int[] faces;

        switch (base) {
            case TETRAHEDRON:
                verts = tetrahedronVertices;
                faces = tetrahedronFaces;
                break;
            case CUBE:
                verts = cubeVertices;
                faces = cubeFaces;
                break;
            case OCTAHEDRON:
            default:
                verts = octahedronVertices;
                faces = octahedronFaces;
                break;
        }

        ModelBuilder mb = new ModelBuilder();
        List<Triangle> triangles = createFaceTriangles(iter);
        for (int i = 0; i < faces.length; i += 3) {
            interpolateTriangle(mb, verts, faces[i],
                    faces[i + 1], faces[i + 2], iter);
            for (Triangle t : triangles) {
                mb.addFace(t, true);
            }
        }
        mb.setRadius(r);

        return mb.toModel();
    }

    protected void interpolateTriangle(ModelBuilder mb, float[] vtx, int vi1,
            int vi2, int vi3, int iter) {
        Point3f v1 =
                new Point3f(vtx[3 * vi1], vtx[3 * vi1 + 1], vtx[3 * vi1 + 2]);
        Point3f v2 =
                new Point3f(vtx[3 * vi2], vtx[3 * vi2 + 1], vtx[3 * vi2 + 2]);
        Point3f v3 =
                new Point3f(vtx[3 * vi3], vtx[3 * vi3 + 1], vtx[3 * vi3 + 2]);
        Point3f vecV = v2.copy().substract(v1).divide((float) (iter + 1));
        Point3f vecH = v3.copy().substract(v2).divide((float) (iter + 1));
        Point3f pBase = v1.copy();
        Point3f pVertex;
        mb.clearVertexCache();

        for (int stepV = 0; stepV < iter + 2; stepV++) {
            if (stepV > 0) {
                pBase.add(vecV);
            }

            pVertex = pBase.copy();
            for (int stepH = 0; stepH < stepV + 1; stepH++) {
                if (stepH > 0) {
                    pVertex.add(vecH);
                }
                mb.addVertex(pVertex);
                // Add normal

                mb.addNormal(pVertex.copy().normalize());
                // Add color
                mb._generateSomeVertexColor(pVertex);
            }
        }
    }

    protected List<Triangle> createFaceTriangles(int iter) {
        // Number of iterated vertices: (iter+1) * (iter+2) / 2;
        int i1 = 0, i2, i3;
        Triangle t;
        List<Triangle> triangles = new ArrayList<Triangle>();
        for (int stepV = 0; stepV < iter + 1; stepV++) {
            for (int stepH = 0; stepH < stepV + 1; stepH++, i1++) {
                i2 = i1 + stepV + 1;
                i3 = i1 + stepV + 2;
                if (stepH > 0) {
                    triangles.add(new Triangle(i1 - 1, i2, i1));
                }
                triangles.add(new Triangle(i1, i2, i3));
            }
        }
        return triangles;
    }
}
