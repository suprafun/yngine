package sk.yin.yngine.math;

import com.sun.opengl.util.texture.Texture;
import sk.yin.yngine.render.shaders.ShaderProgram;
import javax.media.opengl.GL;
import sk.yin.yngine.util.Log;

/**
 * Represents a mesh model. Every face verticle has associated table indexes of
 * normals, color, texture coordinates, etc. if they're present the table aren't
 * null.
 * 
 * @author Matej 'Yin' Gagyi (yinotaurus+yngine-src@gmail.com)
 */
public class Model {
    private float[] vertices;
    private float[] normals;
    private float[] colors;
    private float[] texCoords;
    private int[] faces;
    private ShaderProgram shader;
    private Texture texture;
    private boolean zCorrectCoord;

    public Model() {
        vertices = new float[]{};
        faces = new int[]{};
    }

    public Model(float[] vertexes, int[] triangles) {
        this.vertices = vertexes;
        this.faces = triangles;
    }

    public Model(float[] vertices, float[] normals, float[] colors,
            float[] texCoords, int[] faces) {
        this.vertices = vertices;
        this.normals = normals;
        this.colors = colors;
        this.texCoords = texCoords;
        this.faces = faces;
    }

    public void render(GL gl) {
        boolean hasNormals = normals != null;
        boolean hasColors = colors != null;
        boolean hasTexCoords = texCoords != null;
        int flen = 3
                + (hasNormals ? 3 : 0)
                + (hasColors ? 3 : 0)
                + (hasTexCoords ? 3 : 0);

        if (texture != null) {
            texture.enable();
            texture.bind();
        } else {
            gl.glDisable(GL.GL_TEXTURE_2D);
        }

        if (shader != null) {
            shader.use(gl);
        } else {
            ShaderProgram.unuseCurrent(gl);
        }

        if (!hasNormals) {
            gl.glNormal3f(0f, 0f, 0f);
        }
        if (!hasColors) {
            gl.glColor3f(1f, 1f, 1f);
        }
        if (!hasTexCoords) {
            gl.glTexCoord2f(0f, 0f);
        }

        gl.glBegin(gl.GL_TRIANGLES);
        for (int i = 0; i < faces.length; i += flen) {
            for (int voff = i; voff < i + 3; voff++) {
                int idx, off = voff;

                if (hasNormals) {
                    off += 3;
                    idx = faces[off];
                    if (idx > -1) {
                        gl.glNormal3fv(normals, idx);
                    }
                }
                if (hasColors) {
                    off += 3;
                    idx = faces[off];
                    if (idx > -1) {
                        gl.glColor3fv(colors, idx);
                    }
                }
                if (hasTexCoords) {
                    off += 3;
                    idx = faces[off];
                    if (idx > -1) {
                        gl.glTexCoord2fv(texCoords, idx);
                    }
                    /*
                    if (texture != null) {
                    float d = this.getTextureCorrection(normals[nidx + 2]),
                    s = normals[nidx] * d,
                    t = normals[nidx + 1] * d;
                    gl.glTexCoord2f(s, t);
                    }
                     */
                }
                idx = faces[voff];
                gl.glVertex3fv(vertices, idx);
            }
        }
        gl.glEnd();

        if (texture != null) {
            texture.disable();
        }
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public void setShader(ShaderProgram shader) {
        this.shader = shader;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public float[] colors() {
        return colors;
    }

    public void textureZCorrectCoord(boolean zCorrectCoord) {
        this.zCorrectCoord = zCorrectCoord;
    }

    /**
     * Computes D - the texture coordinate correction coefficient.
     *
     * @param nz
     * @return
     */
    private float getTextureCorrection(float nz) {
        if (zCorrectCoord) {
            return 1.0f / (float) (Math.sin(Math.abs(nz)) + 1.0f);
        } else {
            return 1;
        }
    }
}
