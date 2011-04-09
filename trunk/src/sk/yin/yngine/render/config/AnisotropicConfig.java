package sk.yin.yngine.render.config;

import javax.media.opengl.GL;
import sk.yin.yngine.util.Log;

/**
 * Setups anisotropic filtering for the renderer.
 * 
 * @author Matej 'Yin' Gagyi <matej.gagyi+yngine.src@gmail.com>
 */
public class AnisotropicConfig {

    private static AnisotropicConfig instance;

    public static AnisotropicConfig instance() {
        if (instance == null) {
            instance = new AnisotropicConfig();
        }
        return instance;
    }

    private AnisotropicConfig() {
    }

    public float anisotropy(GL gl) {
        if (isSupported(gl)) {
            return getMaxAnisotropy(gl);
        } else {
            return 0;
        }
    }

    public void anisotropy(GL gl, float anisotropy) {
        if (isSupported(gl)) {
            setAnisotropy(gl, anisotropy);
        }
    }

    public void setMaxAnisotropy(GL gl) {
        if (isSupported(gl)) {
            setAnisotropy(gl, getMaxAnisotropy(gl));
        }
    }

    public boolean isSupported(GL gl) {
        return gl.isExtensionAvailable("GL_EXT_texture_filter_anisotropic");
    }

    private void setAnisotropy(GL gl, float anisotropy) {
        Log.log("Enabling anisotropic filtering: " + anisotropy + "X");
        gl.glTexParameterf(GL.GL_TEXTURE_2D,
                GL.GL_TEXTURE_MAX_ANISOTROPY_EXT,
                anisotropy);
    }

    private float getMaxAnisotropy(GL gl) {
        float max[] = new float[1];
        gl.glGetFloatv(GL.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, max, 0);
        return max[0];
    }
}
