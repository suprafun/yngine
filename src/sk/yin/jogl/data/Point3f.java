package sk.yin.jogl.data;

public class Point3f {
    public static final Point3f BASE = new Point3f();
    public float x, y, z;

    public Point3f() {
    }

    public Point3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //
    // 3D Vector Math
    //

    /**
     * |v1 x v2| = |v1| |v2| * sin(a)
     * @param v
     * @return
     */
    public Point3f cross(Point3f v) {
        float cx = y * v.z - z * v.y,
            cy = z * v.x - x * v.z,
            cz = x * v.y - y * v.x;
        x = cx;
        y = cy;
        z = cz;
        return this;
    }

    /**
     * v1.v2 = |v1| |v2| * cos(a)
     * @param v
     * @return
     */
    public float dot(Point3f v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * puv = (u.v / |v|) * v
     * @param v
     * @return
     */
    public Point3f project(Point3f v) {
        return multiply(dot(v) / v.absolute());
    }

    public Point3f add(float f) {
        x += f;
        y += f;
        z += f;
        return this;
    }

    public Point3f add(Point3f v) {
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Point3f substract(float f) {
        x -= f;
        y -= f;
        z -= f;
        return this;
    }

    public Point3f substract(Point3f v) {
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public Point3f multiply(float f) {
        x *= f;
        y *= f;
        z *= f;
        return this;
    }

    public Point3f multiply(Point3f v) {
        x *= v.x;
        y *= v.y;
        z *= v.z;
        return this;
    }

    public Point3f divide(float f) {
        x /= f;
        y /= f;
        z /= f;
        return this;
    }

    public Point3f divide(Point3f v) {
        x /= v.x;
        y /= v.y;
        z /= v.z;
        return this;
    }

    public Point3f normalize() {
        return divide(absolute());
    }

    public Point3f absolute(float radius) {
        this.multiply(radius / absolute());
        return this;
    }


    public float absolute() {
        Point3f sq = this.copy().multiply(this);
        return (float) Math.sqrt(sq.x + sq.y + sq.z);
    }

    //
    // Common
    //
    public Point3f copy() {
        return new Point3f(x, y, z);
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Point3f) {
            Point3f v = (Point3f) o;
            return Math.abs(x-v.x) < 0.0001f
                && Math.abs(y-v.y) < 0.0001f
                && Math.abs(z-v.z) < 0.0001f;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + x + "," + y + "," + z + "]";
    }

    //
    // RGB Accessors
    //

    public float r() {
        return x;
    }

    public Point3f r(float r) {
        x = r;
        return this;
    }

    public float g() {
        return y;
    }

    public Point3f g(float g) {
        y = g;
        return this;
    }

    public float b() {
        return z;
    }
    public Point3f b(float b) {
        z = b;
        return this;
    }
}
