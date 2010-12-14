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
    private float rf;

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
            gl.glNormal3f(0f, 1f, 0f);
        }
        if (!hasColors) {
            gl.glColor3f(1f, 1f, 1f);
        }
        if (!hasTexCoords) {
            gl.glTexCoord2f(0f, 0f);
        }
        //*
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
                }
                idx = faces[voff];
                gl.glVertex3fv(vertices, idx);
            }
        }
        gl.glEnd();
        // */

        renderNormals(gl, flen);

        if (texture != null) {
            texture.disable();
        }
    }

    protected void renderNormals(GL gl, int flen) {
        boolean hasNormals = normals != null;

        if (hasNormals) {
            ShaderProgram.unuseCurrent(gl);
            gl.glDisable(GL.GL_LIGHTING);
            gl.glDisable(GL.GL_TEXTURE_2D);
            gl.glEnable(GL.GL_LINE_SMOOTH);
            gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_FASTEST);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

            for (int i = 0; i < faces.length; i += flen) {
                for (int voff = i; voff < i + 3; voff++) {
                    int idx;

                    idx = faces[voff];
                    gl.glPushMatrix();
                    gl.glTranslatef(vertices[idx], vertices[idx + 1], vertices[idx + 2]);
                    idx = faces[voff + 3];
                    if (idx > 0) {
                        gl.glBegin(GL.GL_LINES);
            gl.glColor4f(1f, 1f, 1f, 1f);
                        gl.glVertex3f(0f, 0f, 0f);
            gl.glColor4f(1f, 1f, 1f, 0.3f);
                        gl.glVertex3f(normals[idx], normals[idx + 1], normals[idx + 2]);
                        gl.glEnd();
                    }
                    gl.glPopMatrix();
                }
            }

            gl.glEnable(GL.GL_LIGHTING);
            gl.glEnable(GL.GL_DEPTH_TEST);
            gl.glDisable(GL.GL_LINE_SMOOTH);
            gl.glDisable(GL.GL_BLEND);
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
}
