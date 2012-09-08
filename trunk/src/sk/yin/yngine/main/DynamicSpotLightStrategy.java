package sk.yin.yngine.main;

import javax.vecmath.Vector3f;

import sk.yin.yngine.scene.GenericLightNode;

/**
 * This controls the spot light circulating around the ground. It smoothly
 * changes diameter of light trajectory to enable sporadical occurence of
 * lighting effects.
 * @author
 */
class DynamicSpotLightStrategy implements IGLRendererStrategy {

    GenericLightNode stopLight;
    GLRenderer outer;

    public DynamicSpotLightStrategy(GenericLightNode stopLight, GLRenderer outer) {
        this.outer = outer;
        this.stopLight = stopLight;
    }

    public void applyStrategy() {
        double dr = Math.sin(outer.r * 1.9);
        double rotSpeed = 0.5;
        double lDist = (Math.cos(outer.r / 2.3) + 1) / 2;
        lDist = 100 - lDist * lDist * 40;
        stopLight.position(new Vector3f((float) (Math.sin(outer.r * rotSpeed) * lDist), 30.0F, (float) (Math.cos(outer.r * rotSpeed) * lDist)));
        stopLight.direction(new Vector3f((float) -Math.sin(outer.r * rotSpeed + dr), -1.55F, (float) -Math.cos(outer.r * rotSpeed + dr)));
    }
}
