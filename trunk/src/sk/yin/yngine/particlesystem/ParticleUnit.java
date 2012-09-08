package sk.yin.yngine.particlesystem;

import javax.media.opengl.GL2;

/**
 * A Particle Unit is a black-box to individual particle system simulations.
 * It provides interface for initializing, stepping, rendering of whole
 * particle systems. By overriding of template methods, track keeping and
 * analyzing is possible.
 * 
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class ParticleUnit {
    private IParticleSystemSimulator simulator;
    private IParticleSystemRenderer renderer;
    private IParticleSystemState state;

    public ParticleUnit(IParticleSystemFactory factory,
            IParticleSystemConfiguration config) {
        simulator = factory.newSimulator();
        state = simulator.configure(config);
        renderer = factory.newRenderer();
        renderer.configure(config);
    }
    
    public void update(float deltaTime) {
        updateTime(deltaTime);
        updateState(simulator.simulate(deltaTime));
    }

    /**
     * Renders this particle unit.
     * @param gl
     */
    public void render(GL2 gl) {
        renderer.render(gl, state);
    }

    /**
     * Time tracking template method.
     * @param deltaTime
     */
    protected void updateTime(float deltaTime) {
    }

    /**
     * Particle system state change template method
     * @param state New particle system state
     */
    protected void updateState(IParticleSystemState state) {
        this.state = state;
    }
}
