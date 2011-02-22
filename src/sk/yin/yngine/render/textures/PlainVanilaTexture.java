package sk.yin.yngine.render.textures;

import com.sun.opengl.util.texture.Texture;
import javax.media.opengl.GL;

/**
 *
 * @author Yin
 */
public class PlainVanilaTexture {
    private Texture texture;
    private int glTexUnit = -1;

    public PlainVanilaTexture(Texture texture) {
        this.texture = texture;
    }

    public int bind(GL gl) {
        if(glTexUnit < 0)
            glTexUnit = TexUnitRepository.instance().allocate();
        //gl.glActiveTexture(glTexUnit);
        texture.enable();
        texture.bind();
        return 0;
    }

    public void unbind(GL gl) {
        //gl.glActiveTexture(glTexUnit);
        texture.disable();
        return;
    }

    public Texture texture() {
        return texture;
    }

    public int texUnit() {
        return glTexUnit - GL.GL_TEXTURE0;
    }

    @Override
    public String toString() {
        return "PlainVanila@" + texture.hashCode();
    }
}
