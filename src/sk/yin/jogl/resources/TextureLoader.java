package sk.yin.jogl.resources;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureIO;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 *
 * @author yin
 */
public class TextureLoader {
    private static TextureLoader instance;

    private TextureLoader() {
    }

    public static TextureLoader getInstance() {
        if (instance == null) {
            instance = new TextureLoader();
        }
        return instance;
    }

    public Texture load(String filename) {
        Texture texture = null;
        try {
            texture = TextureIO.newTexture(new File(filename), true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return texture;
    }

    public Texture load(URL url) {
        Texture texture = null;
        try {
            texture = TextureIO.newTexture(url, true, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return texture;
    }
}
