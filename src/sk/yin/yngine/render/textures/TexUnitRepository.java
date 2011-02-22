package sk.yin.yngine.render.textures;

import javax.media.opengl.GL;

/**
 * @author Yin
 */
public class TexUnitRepository {
    private static final int FREE = -1;
    private static TexUnitRepository instance = null;
    private int texUnits[] = new int[GL.GL_MAX_LIGHTS];

    public static TexUnitRepository instance() {
        if (instance == null) {
            instance = new TexUnitRepository();
        }
        return instance;
    }

    private TexUnitRepository() {
        for(int i = 0; i < texUnits.length; i++)
            texUnits[i] = FREE;
    }

    public int allocate() {
        for (int i = 0; i < texUnits.length; i++) {
            if (texUnits[i] == FREE) {
                texUnits[i] = 1;
                // OpenGL texUnits map to successive numeric values.
                return GL.GL_TEXTURE0 + i;
            }
        }
        return -1;
    }

    public int release(int glTexUnit) {
        if (isGLTextureUnit(glTexUnit)) {
            int index = glTexUnit - GL.GL_TEXTURE0;
            int ret = texUnits[index];
            texUnits[index] = FREE;
            return ret;
        }
        return FREE;
    }

    protected static boolean isGLTextureUnit(int glTexUnit) {
        return glTexUnit >= GL.GL_TEXTURE0
                && glTexUnit < (GL.GL_TEXTURE0 + GL.GL_MAX_TEXTURE_UNITS);
    }
}
