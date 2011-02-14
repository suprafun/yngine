package sk.yin.yngine.scene;

import sk.yin.yngine.render.lights.GLLightRepository;
import javax.media.opengl.GL;
import javax.vecmath.Vector3f;
import sk.yin.yngine.render.shaders.ShaderProgram;
import sk.yin.yngine.scene.attributes.ISceneAttribute;

/**
 * Directional light.
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class GenericLightNode implements ILightNode {

    public Vector3f position = new Vector3f(),
            direction = new Vector3f();
    public float ambient[], diffuse[], specular[];
    private int glLight = -1;
    private LightType type = LightType.Point;

    public GenericLightNode(){
    }

    public GenericLightNode(LightType type) {
        this.type = type;
    }

    public enum LightType {

        Directional(0.0f),
        Point(1.0f),
        Spot(1.0f);
        public float w;

        LightType(float w) {
            this.w = w;
        }
    }

    public void turnOn(GL gl, int glLight) {
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

            switch (type) {
                case Directional:
                    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{
                                direction.x, direction.y, direction.z, type.w}, 0);
                    break;
                case Spot:
                    gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, new float[]{
                                direction.x, direction.y, direction.z}, 0);
                    gl.glLightf(GL.GL_LIGHT0, GL.GL_SPOT_DIRECTION, 0);

                case Point:
                    gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, new float[]{
                                direction.x, direction.y, direction.z, type.w}, 0);
                    break;
                default:

            }
        }
    }

    public void turnOff(GL gl, int glLight) {
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
            turnOn(gl, glLight);
        }
        gl.glDisable(GL.GL_LIGHTING);
        ShaderProgram.unuseCurrent(gl);
        gl.glBegin(GL.GL_POINTS);
        gl.glVertex3f(direction.x, direction.y, direction.z);
        gl.glEnd();
        gl.glEnable(GL.GL_LIGHTING);
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

    public void setPosition(Vector3f position) {
        this.position.set(position);
    }

    public float[] ambient() {
        return ambient;
    }
    public void ambient(float[] ambient) {
        this.ambient = ambient;
    }

    public float[] diffuse() {
        return diffuse;
    }
    public void diffuse(float[] diffuse) {
        this.diffuse = diffuse;
    }

    public float[] specular() {
        return specular;
    }
    public void specular(float[] specular) {
        this.specular = specular;
    }
}
