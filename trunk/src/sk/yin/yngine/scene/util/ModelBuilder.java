package sk.yin.yngine.scene.util;

import sk.yin.yngine.math.Model;
import sk.yin.yngine.math.Triangle;
import sk.yin.yngine.math.Point3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

// TODO(magyi): Implement Absent, PerFace (Normals done) and PerFaceVertice ModelLayouts
public class ModelBuilder {
    private List<Point3f> vertices = new ArrayList<Point3f>();
    public List<Integer> verticleCache = new ArrayList<Integer>();
    private List<Point3f> colors = new ArrayList<Point3f>();
    private List<Point3f> normals = new ArrayList<Point3f>();
    private List<Triangle> faces = new ArrayList<Triangle>();
    private List<Integer> faceColors = new ArrayList<Integer>();
    private Map<Integer, Integer> faceNormals = new HashMap<Integer, Integer>();
    private int lastFace = -1;

    public ModelBuilder() {
    }

    public int addVertex(Point3f v) {
        int idx = vertices.indexOf(v);
        if (idx == -1) {
            vertices.add(v.copy());
            idx = vertices.size() - 1;
        }
        verticleCache.add(idx);
        return idx;
    }

    public int _generateSomeVertexColor(Point3f v) {
        Point3f c = new Point3f();
        // TODO(mgagyi): Implement these switches as Strategy pattern
        /*
        float rr = (float) (Math.random() / 3 - (1.0 / 6));
        float rg = (float) (Math.random() / 3 - (1.0 / 6));
        float rb = (float) (Math.random() / 3 - (1.0 / 6));
        c.x = (float) Math.random();
        c.y = (float) Math.random();
        c.z = (float) Math.random();
        /*/
        float rr = (float) Math.sin(Math.PI * v.x * 2) / 4;
        rr = 0;
        c.x = (float) Math.sin(v.x * 3);
        c.y = (float) Math.sin(v.y * 3);
        c.z = (float) Math.sin(v.z * 3);
        //*/

        return addColor(c);
    }

    public int addColor(Point3f c) {
        int idx = colors.indexOf(c);
        if (idx == -1) {
            colors.add(c.copy());
            idx = colors.size() - 1;
        }
        return idx;
    }

    public int addNormal(Point3f n) {
        int idx = normals.indexOf(n);
        if (idx == -1) {
            normals.add(n.copy());
            idx = normals.size() - 1;
        }
        return idx;
    }

    public int addFace(Triangle t) {
        return addFace(t, false);
    }

    public int addFace(Triangle t, boolean mapFace) {
        if (mapFace) {
            t = mapFaceIndexes(t);
        }

        int idx = faces.indexOf(t);
        if (idx == -1) {
            idx = faces.size();
            faces.add(t);
        }
        return lastFace = idx;
    }

    public void setLastFaceNormals(int... normals) {
        if(normals.length != 1)
            throw new IllegalArgumentException("normals.length != 1");
        faceNormals.put(lastFace, normals[0]);
    }

    public int setLastFaceColors(int... colors) {
        throw new NotImplementedException();
    }

    /**
     * Maps vertex indexes of a given face to model vertex indexes.
     * @param t
     * @return
     */
    protected Triangle mapFaceIndexes(Triangle t) {
        return new Triangle(verticleCache.get(t.v1), verticleCache.get(t.v2), verticleCache.get(t.v3));
    }

    public void clearVertexCache() {
        verticleCache.clear();
    }

    public void setRadius(float radius) {
        for (Point3f v : vertices) {
            v.absolute(radius);
        }
    }

    public Model toModel() {
        Model.ModelLayout normalLayout = Model.ModelLayout.VerticleBound,
                colorLayout = Model.ModelLayout.VerticleBound;
        return toModel(normalLayout, colorLayout);
    }

    public Model toModel(Model.ModelLayout colorLayout, Model.ModelLayout normalLayout) {
        int flen = 3 + normalLayout.len + colorLayout.len;

        float[] vs = new float[vertices.size() * 3];
        float[] cs = new float[colors.size() * 3];
        float[] ns = new float[normals.size() * 3];
        int[] ts = new int[faces.size() * flen];

        for (int i = 0; i < vertices.size(); i++) {
            Point3f v = vertices.get(i);
            vs[3 * i] = v.x;
            vs[3 * i + 1] = v.y;
            vs[3 * i + 2] = v.z;
        }
        for (int i = 0; i < colors.size(); i++) {
            Point3f c = colors.get(i);
            cs[3 * i] = c.x;
            cs[3 * i + 1] = c.y;
            cs[3 * i + 2] = c.z;
        }
        for (int i = 0; i < normals.size(); i++) {
            Point3f n = normals.get(i);
            ns[3 * i] = n.x;
            ns[3 * i + 1] = n.y;
            ns[3 * i + 2] = n.z;
        }
        for (int i = 0; i < faces.size(); i++) {
            Triangle t = faces.get(i);
            int fi = flen * i;
            ts[fi++] = t.v1;
            ts[fi++] = t.v2;
            ts[fi++] = t.v3;

            if (normalLayout == Model.ModelLayout.VerticleBound) {
            } else if (normalLayout == Model.ModelLayout.PerFace) {
                ts[fi++] = faceNormals.get(i);
            } else {
                throw new NotImplementedException();
            }
        }

        return new Model(vs, ns, cs, ts, normalLayout, colorLayout);
    }

}
