package sk.yin.yngine.scene.io;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.media.opengl.GL2;

import sk.yin.yngine.resources.ResourceGetter;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 *
 * @author yin
 */
public class TextureLoader {

    private static TextureLoader instance;
    private TextureConfig defaultConfig;

    private TextureLoader() {
        defaultConfig = new TextureConfig();
    }

    public static TextureLoader getInstance() {
        if (instance == null) {
            instance = new TextureLoader();
        }
        return instance;
    }

    public Texture[] loadResource(GL2 gl, String[] filenames) {
        List<URL> urls = ResourceGetter.getResources(filenames);
        Texture[] textures = new Texture[urls.size()];
        int i = 0;
        for (URL url : urls) {
            textures[i++] = load(gl, url);
        }
        return textures;
    }

    public Texture load(GL2 gl, URL url) {
        Texture texture = null;
        try {
            // TODO(yin): Create better mipmap the the autogeneration algorithm.
            texture = TextureIO.newTexture(url, true, null);
            if (defaultConfig != null) {
                defaultConfig.applyConfig(gl, texture);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return texture;
    }
}
