package sk.yin.jogl.render.particlesystem;

import javax.media.opengl.GL;
import sk.yin.jogl.data.Point3f;

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

    public void render(GL gl, SimpleState state) {
        Point3f ps[] = state.particles(),
                vs[] = state.velocities();

        preRender(gl);
        gl.glBegin(GL.GL_POINTS);
        for(int i = 0, c = ps.length; i < c; i++) {
            Point3f color = ps[i].copy().divide(40.0f).add(0.75f);
            gl.glColor3f(color.r(), color.g(), color.b());
            gl.glVertex3f(ps[i].x, ps[i].y, ps[i].z);
        }
        gl.glEnd();
        postRender(gl);
    }

    public void render(GL gl, IParticleSystemState state) {
        if(state instanceof SimpleState) {
            SimpleState ss = (SimpleState) state;
            render(gl, ss);
        } else {
            throw new IllegalArgumentException("Particle system State of unknown type.");
        }
    }

    protected void preRender(GL gl) {
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glDisable(GL.GL_LIGHTING);
        gl.glPointSize(1.5f);
    }

    protected void postRender(GL gl) {
        //gl.glEnable(GL.GL_LIGHTING);
        gl.glPointSize(1.0f);
    }
}
