/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.jogl.resources;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class CubeMapTextureFactory {
    private static GL gl;
    private static CubeMapTextureFactory instance;

    private CubeMapTextureFactory() {
    }

    public static CubeMapTextureFactory getInstance(GL gl) {
        if(instance == null || gl == null) {
            instance = new CubeMapTextureFactory();
            CubeMapTextureFactory.gl = gl;
        } else if (CubeMapTextureFactory.gl != gl) {
            // TODO(mgagyi): Support multiple GL instances
            throw new IllegalArgumentException("Currently only one GL instance can be used in an application.");
        }
        return instance;
    }

    public void loadImage(String filename) throws IOException {
        int[] coords = new int[] {
            512, 512, GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            1536, 512, GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z,
            512, 0, GL.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            512, 1024, GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            0, 512, GL.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            1024, 512, GL.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
        };
        BufferedImage side,
                image = ImageIO.read(getClass().getResource(filename));

        Texture texture = TextureIO.newTexture(GL.GL_TEXTURE_CUBE_MAP);
        gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
        texture.setTexParameteri(GL.GL_GENERATE_MIPMAP_SGIS, GL.GL_TRUE);

        // texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        // texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
        texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);

        for(int i = 0; i < coords.length; i += 3) {
            side = image.getSubimage(coords[i], coords[i+1], 512, 512);
            TextureData sideData = TextureIO.newTextureData(side, false);
            texture.updateImage(sideData, coords[i+3]);
        }
    }
}
