package sk.yin.yngine.scene.attributes;

import javax.media.opengl.GL2;

import sk.yin.yngine.particlesystem.ParticleUnit;

/**
 * Particle system adaptor for scene graph.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class ParticleUnitAttribute implements ISceneAttribute {
    private ParticleUnit unit;

    public ParticleUnitAttribute(ParticleUnit unit) {
        this.unit = unit;
    }

    /**
     * Updates particle system by <code>deltaTime</code> seconds (if seconds is
     * time unit).
     *
     * @param deltaTime Time to step simulation of particles.
     */
    public void update(float deltaTime) {
        unit.update(deltaTime);
    }

    public void render(GL2 gl, RenderStage stage) {
        // TODO(yin): Add RENDER_TRANSPARENT to RenderStage enum.
        if(stage == RenderStage.RENDER)
            unit.render(gl);
    }
}
