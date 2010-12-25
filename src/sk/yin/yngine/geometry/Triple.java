package sk.yin.yngine.geometry;

/**
 * Collection of 3 indexes, e.g. for triangle vertex coordinates, colors,
 * normals, etc.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class Triple {
    public int idx1;
    public int idx2;
    public int idx3;

    public Triple() {
    }

    public Triple(int idx1, int idx2, int idx3) {
        this.idx1 = idx1;
        this.idx2 = idx2;
        this.idx3 = idx3;
    }

    public Triple(int idx) {
        idx1 = idx2 = idx3 = idx;
    }

    @Override
    public String toString() {
        return idx1 + "-" + idx2 + "-" + idx3;
    }
}
