package sk.yin.jogl.scene;

import javax.media.opengl.GL;
import sk.yin.jogl.render.particlesystem.ParticleUnit;

/**
 * Particle system and scene graph adaptor.
 * 
 * @author Matej 'Yin' Gagyi (matej.gagi@gmail.com)
 */
public class ParticleUnitSceneNode implements ISceneNode {
    private ParticleUnit unit;

    public ParticleUnitSceneNode(ParticleUnit unit) {
        this.unit = unit;
    }

    public void render(GL gl) {
        unit.render(gl);
    }

    public void update(float deltaTime) {
        unit.update(deltaTime);
    }
}
