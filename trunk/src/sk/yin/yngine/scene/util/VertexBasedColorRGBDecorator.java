package sk.yin.yngine.scene.util;

import java.util.HashMap;
import java.util.Map;
import sk.yin.yngine.math.Point3f;
import sk.yin.yngine.math.Triple;

/**
 * Cycles red color on X axis, green on Y, blue on Z, given the vertex
 * translation.
 *
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class VertexBasedColorRGBDecorator extends BaseDecorator {
    Map<Integer,Integer> map = new HashMap<Integer,Integer>();
    @Override
    public void onNewVertex(int idx, Point3f vertex) {
        if(builder != null) {
            int i = builder.addColor(vertex);
            map.put(idx, i);
        }
    }

    @Override
    public void onNewFace(int idx, Triple face) {
        if(builder != null) {
            builder.appendColorIndexes(new Triple(
                    map.get(face.idx1),
                    map.get(face.idx2),
                    map.get(face.idx3)));
        }
    }
}
