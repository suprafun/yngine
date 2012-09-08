package sk.yin.yngine.render.textures;

import javax.media.opengl.GL2;

import com.jogamp.opengl.util.texture.Texture;

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

    public int bind(GL2 gl) {
        if(glTexUnit < 0)
            glTexUnit = TexUnitRepository.instance().allocate();
        //gl.glActiveTexture(glTexUnit);
        texture.enable(gl);
        texture.bind(gl);
        return 0;
    }

    public void unbind(GL2 gl) {
        //gl.glActiveTexture(glTexUnit);
        texture.disable(gl);
        return;
    }

    public Texture texture() {
        return texture;
    }

    public int texUnit() {
        return glTexUnit - GL2.GL_TEXTURE0;
    }

    @Override
    public String toString() {
        return "PlainVanila@" + texture.hashCode();
    }
}
