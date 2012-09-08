package sk.yin.yngine.particlesystem;

import javax.media.opengl.GL2;

import sk.yin.yngine.geometry.Point3f;
import sk.yin.yngine.render.shaders.ShaderProgram;

/**
 *
 * @author yin
 */
public class SimpleRenderer implements IParticleSystemRenderer {
    public SimpleRenderer() {
    }

    public void configure(IParticleSystemConfiguration config) {
        // Do nothing
    }

    public void render(GL2 gl, SimpleState state) {
        Point3f ps[] = state.particles(),
                vs[] = state.velocities();

        preRender(gl);
        gl.glBegin(GL2.GL_POINTS);
        for(int i = 0, c = ps.length; i < c; i++) {
            Point3f color = ps[i].copy().divide(40.0f).add(0.75f);
            gl.glColor3f(color.r(), color.g(), color.b());
            gl.glVertex3f(ps[i].x, ps[i].y, ps[i].z);
        }
        gl.glEnd();
        postRender(gl);
    }

    public void render(GL2 gl, IParticleSystemState state) {
        if(state instanceof SimpleState) {
            SimpleState ss = (SimpleState) state;
            render(gl, ss);
        } else {
            throw new IllegalArgumentException("Particle system State of unknown type.");
        }
    }

    protected void preRender(GL2 gl) {
        ShaderProgram.unuseCurrent(gl);
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(GL2.GL_LIGHTING);
        gl.glPointSize(1.5f);
    }

    protected void postRender(GL2 gl) {
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glPointSize(1.0f);
    }
}
