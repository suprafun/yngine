package sk.yin.yngine.particlesystem;

/**
 *
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public interface IParticleSystemFactory {
    public IParticleSystemSimulator newSimulator();
    public IParticleSystemRenderer newRenderer();
}
