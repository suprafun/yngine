package sk.yin.jogl.render.particlesystem;

/**
 *
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public class SimpleFactory implements IParticleSystemFactory {

    public IParticleSystemSimulator newSimulator() {
        return new SimpleSimulator();
    }

    public IParticleSystemRenderer newRenderer() {
        return new SimpleRenderer();
    }

}
