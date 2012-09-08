package sk.yin.yngine.particlesystem;

import javax.media.opengl.GL2;

/**
 *
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public interface IParticleSystemRenderer {
    public void configure(IParticleSystemConfiguration config);
    public void render(GL2 gl, IParticleSystemState state);
}
