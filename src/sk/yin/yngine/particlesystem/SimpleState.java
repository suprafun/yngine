package sk.yin.yngine.particlesystem;

import java.util.HashMap;
import java.util.Map;
import sk.yin.yngine.geometry.Point3f;

/**
 * Minimal particle system state with gravity and air invResistance.
 *
 * @author Matej 'Yin' Gagyi (yinotarus+yngine-src@gmail.com)
 */
public class SimpleState implements IParticleSystemState {
    private Point3f[] positions;
    private Point3f[] velocities;
    private Point3f gravity;
    private float resistance;
    private Map<String, IParticleSystemState> siblings;
    
    public SimpleState(int count, Point3f gravity, float resistance) {
        positions = new Point3f[count];
        velocities = new Point3f[count];
        siblings = new HashMap<String, IParticleSystemState>();
        this.gravity = gravity;
        this.resistance = resistance;

        for(int i = 0; i < count; i++) {
            positions[i] = new Point3f();
            velocities[i] = new Point3f();
        }
    }

    public Point3f[] particles() {
        return positions;
    }

    public Point3f[] velocities() {
        return velocities;
    }

    public Point3f gravity() {
        return gravity;
    }
    
    public SimpleState gravity(Point3f gravity) {
        this.gravity = gravity;
        return this;
    }

    public float invResistance() {
        return resistance;
    }

    public SimpleState invResistance(float resistance) {
        this.resistance = resistance;
        return this;
    }

    public IParticleSystemState getSibling(String identifier) {
        return siblings.get(identifier);
    }
}
