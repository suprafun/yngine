package sk.yin.jogl.render.particlesystem;

import javax.media.opengl.GL;

/**
 *
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public interface IParticleSystemRenderer {
    public void configure(IParticleSystemConfiguration config);
    public void render(GL gl, IParticleSystemState state);
}
