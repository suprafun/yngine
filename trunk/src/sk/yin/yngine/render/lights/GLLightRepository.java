package sk.yin.yngine.render.lights;

import javax.media.opengl.GL;

/**
 * A repository of allocable OpenGL lights.
 * @author yin
 */
public class GLLightRepository {
    private static GLLightRepository instance = null;
    private boolean lights[] = new boolean[GL.GL_MAX_LIGHTS];

    public static GLLightRepository instance() {
        if (instance == null) {
            instance = new GLLightRepository();
        }
        return instance;
    }

    private GLLightRepository() {
        for(int i = 0; i < lights.length; i++)
            lights[i] = false;
    }

    public int allocate() {
        for (int i = 0; i < lights.length; i++) {
            if (lights[i] == false) {
                lights[i] = true;
                // OpenGL lights map to successive numeric values.
                return GL.GL_LIGHT0 + i;
            }
        }
        return -1;
    }

    public boolean release(int glLight) {
        if (isGLLight(glLight)) {
            boolean ret = lights[glLight - GL.GL_LIGHT0];
            lights[glLight - GL.GL_LIGHT0] = false;
            return ret;
        }
        return false;
    }

    public static boolean isGLLight(int glLight) {
        return glLight >= GL.GL_LIGHT0 && glLight < (GL.GL_LIGHT0 + GL.GL_MAX_LIGHTS);
    }
}
