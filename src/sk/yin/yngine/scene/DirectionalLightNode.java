package sk.yin.yngine.scene;

import sk.yin.yngine.render.lights.GLLightRepository;
import javax.media.opengl.GL;
import javax.vecmath.Vector3f;
import sk.yin.yngine.scene.attributes.ISceneAttribute;

/**
 * Directional light.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class DirectionalLightNode implements ILightNode {
    public Vector3f direction = new Vector3f();
    public float ambient[], diffuse[], specular[];
    private int glLight = -1;

    public void activateLight(GL gl, int glLight) {
        if (isGLLight(glLight)) {
            gl.glEnable(glLight);

            if (ambient != null) {
                gl.glLightfv(glLight, GL.GL_AMBIENT, ambient, 0);
            } else {
                gl.glLightfv(glLight, GL.GL_AMBIENT, new float[]{0f, 0f, 0f, 0f}, 0);
            }
            if (diffuse != null) {
                gl.glLightfv(glLight, GL.GL_DIFFUSE, diffuse, 0);
            } else {
                gl.glLightfv(glLight, GL.GL_DIFFUSE, new float[]{0f, 0f, 0f, 0f}, 0);
            }
            if (specular != null) {
                gl.glLightfv(glLight, GL.GL_SPECULAR, specular, 0);
            } else {
                gl.glLightfv(glLight, GL.GL_SPECULAR, new float[]{0f, 0f, 0f, 0f}, 0);
            }
            if (direction != null) {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{
                            direction.x, direction.y, direction.z, 0.0f}, 0);
            } else {
                gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{
                            0, 1, 0, 0.0f}, 0);
            }

        }
    }

    public void deactivateLight(GL gl, int glLight) {
        if (isGLLight(glLight)) {
            gl.glDisable(glLight);
        }
    }

    protected boolean isGLLight(int glLight) {
        return glLight >= GL.GL_LIGHT0 && glLight <= GL.GL_LIGHT7;
    }

    public void update(float deltaTime) {
    }

    // TODO(yin): This has to be moved to renderer.
    public void render(GL gl) {
        if (glLight == -1) {
            glLight = GLLightRepository.instance().allocate();
        }
        if (glLight != -1) {
            // TODO(yin): This has to be reversable.
            activateLight(gl, glLight);
        }
    }

    public void onAdded(SceneGraph graph, ISceneAttribute parent) {
    }

    public void onRemoved(SceneGraph graph) {
    }

    public <X extends ISceneAttribute> X attribute(Class<X> type) {
        return null;
    }

    public <X extends ISceneAttribute> X attribute(Class<X> type, String query) {
        return null;
    }

    public boolean addChild(ISceneNode child) {
        return false;
    }

    public void setDirection(Vector3f direction) {
        this.direction.set(direction);
        this.direction.normalize();
    }

    public float[] ambient() {
        return ambient;
    }

    public float[] diffuse() {
        return diffuse;
    }

    public float[] specular() {
        return specular;
    }
}
