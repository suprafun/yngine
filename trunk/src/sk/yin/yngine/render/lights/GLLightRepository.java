package sk.yin.yngine.render.lights;

import javax.media.opengl.GL;

/**
 * A repository of lights.
 * @author yin
 */
public class GLLightRepository {
    private static GLLightRepository instance = null;
    private boolean lights[] = new boolean[8];

    public static GLLightRepository instance() {
        if (instance == null) {
            instance = new GLLightRepository();
        }
        return instance;
    }

    private GLLightRepository() {
    }

    public int allocate() {
        for (int i = 0; i < 8; i++) {
            if (lights[i] == false) {
                lights[i] = true;
                // OpenGL lights map to successive numeric values.
                return GL.GL_LIGHT0 + i;
            }
        }
        return -1;
    }

    public boolean release(int glLight) {
        if (glLight >= GL.GL_LIGHT0 && glLight <= GL.GL_LIGHT7) {
            boolean ret = lights[glLight - GL.GL_LIGHT0];
            lights[glLight - GL.GL_LIGHT0] = false;
            return ret;
        }
        return false;
    }
}
