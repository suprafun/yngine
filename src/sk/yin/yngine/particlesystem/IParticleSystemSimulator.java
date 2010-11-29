package sk.yin.yngine.particlesystem;

/**
 *
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public interface IParticleSystemSimulator {
    public IParticleSystemState configure(IParticleSystemConfiguration config);
    public IParticleSystemState simulate(float deltaTime);
}
