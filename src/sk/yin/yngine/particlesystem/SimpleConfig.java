package sk.yin.yngine.particlesystem;

import sk.yin.yngine.geometry.Point3f;

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
