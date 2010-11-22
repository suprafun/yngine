package sk.yin.jogl.render.particlesystem;

import sk.yin.jogl.data.Point3f;

/**
 *
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public class SimpleSimulator implements IParticleSystemSimulator {

    private SimpleState state;
    private float boundingBox, boundingBoxBounce;
    int n;

    public IParticleSystemState configure(IParticleSystemConfiguration config) {
        if (config instanceof SimpleConfig) {
            SimpleConfig sc = (SimpleConfig) config;
            state = new SimpleState(sc.count, sc.gravity, sc.invResistance);
            boundingBox = sc.boundingBox;
            boundingBoxBounce = sc.boundingBoxBounce;

            // Randomize particle velocities to create an "exploding" behavior
            Point3f vs[] = state.velocities();
            for (Point3f v : vs) {
                v.x = (float) Math.random() - 0.5f;
                v.y = (float) Math.random() - 0.5f;
                v.z = (float) Math.random() - 0.5f;
                v.absolute(12.0f + (float) Math.random() / 5);
            }
        }
        return state;
    }

    public IParticleSystemState simulate(float dt) {
        Point3f ps[] = state.particles(),
                vs[] = state.velocities(),
                gs = state.gravity();
        float ir = state.invResistance();
        int c = ps.length;

        for (int i = 0; i < c; i++) {

            // Position delta
            Point3f v = vs[i];
            // Gravity, Resistance
            v.add(gs).multiply(ir);
            // Resistance
            ps[i].add(v.copy().multiply(dt));
            
            // Bounding box colision
            if (boundingBox > 0) {
                Point3f p = ps[i];
                if (p.x > boundingBox
                        || p.x < -boundingBox) {
                    p.x = Math.signum(p.x) * boundingBox;
                    vs[i].x = -vs[i].x * boundingBoxBounce;
                }
                if (p.y > boundingBox
                        || p.y < -boundingBox) {
                    p.y = Math.signum(p.y) * boundingBox;
                    vs[i].y = -vs[i].y * boundingBoxBounce;
                }
                if (p.z > boundingBox
                        || p.z < -boundingBox) {
                    p.z = Math.signum(p.z) * boundingBox;
                    vs[i].z = -vs[i].z * boundingBoxBounce;
                }
            }
        }
        n++;
        return state;
    }
}
