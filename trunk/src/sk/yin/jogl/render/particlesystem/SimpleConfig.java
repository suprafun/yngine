package sk.yin.jogl.render.particlesystem;

import sk.yin.jogl.data.Point3f;

/**
 * Configuration for minimal 'simple' particle system.
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public class SimpleConfig implements IParticleSystemConfiguration {
    public int count;
    public Point3f gravity;
    public float invResistance;
    public float boundingBox, boundingBoxBounce;
}
