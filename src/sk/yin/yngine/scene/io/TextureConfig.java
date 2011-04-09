package sk.yin.yngine.scene.io;

import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;

/**
 *
 * @author Yin
 */
public class TextureConfig {

    public void applyConfig(Texture texture) {
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
        texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
    }
}
