package sk.yin.yngine.scene.io;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

/**
 *
 * @author Yin
 */
public class TextureConfig {

    public void applyConfig(GL2 gl, Texture texture) {
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
    }
}
