package sk.yin.jogl.data;

import sk.yin.jogl.data.Point3f;

public class Triangle {
    public int v1;
    public int v2;
    public int v3;

    public Triangle() {
    }

    public Triangle(int v1, int v2, int v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public Point3f nearest(Point3f point) {
        return null;
    }

    public float intersect(Point3f point, Point3f ray) {
        return 0.0f;
    }

    @Override
    public String toString() {
        return v1 + "-" + v2 + "-" + v3;
    }
}
