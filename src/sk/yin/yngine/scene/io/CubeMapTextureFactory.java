/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.yngine.scene.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 *
 * @author yin
 */
public class CubeMapTextureFactory {
    private static GL2 gl;
    private static CubeMapTextureFactory instance;

    private CubeMapTextureFactory() {
    }

    public static CubeMapTextureFactory getInstance(GL2 gl) {
        if(instance == null || gl == null) {
            instance = new CubeMapTextureFactory();
            CubeMapTextureFactory.gl = gl;
        } else if (CubeMapTextureFactory.gl != gl) {
            // TODO(mgagyi): Support multiple GL2 instances
            throw new IllegalArgumentException("Currently only one GL2 instance can be used in an application.");
        }
        return instance;
    }

    public Texture loadImage(URL url) throws IOException {
        int[] coords = new int[] {
            512, 512, GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Z,
            1536, 512, GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z,
            512, 0, GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_X,
            512, 1024, GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_X,
            0, 512, GL2.GL_TEXTURE_CUBE_MAP_POSITIVE_Y,
            1024, 512, GL2.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y
        };
        BufferedImage side,
                image = ImageIO.read(url);

        Texture texture = TextureIO.newTexture(GL2.GL_TEXTURE_CUBE_MAP);
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 1);
        texture.setTexParameteri(gl, GL2.GL_GENERATE_MIPMAP_HINT, GL2.GL_TRUE);

        // texture.setTexParameteri(GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        // texture.setTexParameteri(GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        texture.setTexParameteri(gl, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

//        for(int i = 0; i < coords.length; i += 3) {
//            side = image.getSubimage(coords[i], coords[i+1], 512, 512);
//            TextureData sideData = TextureIO.newTextureData(GLProfile.getGL2GL3(), side, false);
//            texture.updateImage(gl, sideData, coords[i+2]);
//        }

        return texture;
    }
}
