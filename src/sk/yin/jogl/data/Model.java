/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.yin.jogl.data;

import com.sun.opengl.util.texture.Texture;
import sk.yin.jogl.shaders.ShaderProgram;
import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class Model {
    //
    // vertices  -> [ v1:(x y z) v2:(x y z) ... ] -> { float, float, float; ... }
    //
    // normals   -> [ n1:(x y z) n2:(x y z) ... ] -> { float, float, float; ... }
    //
    // TODO(mgagyi): RGBA support
    // colors    -> [ c1:(r g b) c2:(r g b) ... ] -> { float, float, float; ... }
    //
    // faces     -> [ f1:(Ai Bi Ci) f2:(Ai Bi Ci) ... ]
    // facesData -> [ (<Normal>+<Color>) ]
    //
    // <Normal> => normalLayout=VerticleBound
    //              -> () -> { int, int, int; ... }
    //                    -> |normals| == |vertices|
    //          => normalLayout=PerFace
    //              -> (n) -> { int, int, int, int; ... }
    //          => normalLayout=PerFaceVerticle
    //              -> (An Bn Cn) -> { int, int, int, int, int, int; ... }
    //
    // <Color> => colorLayout=VerticleBound
    //              -> () -> |colors| == |vertices|
    //            => colorLayout=PerFace
    //              -> (c)
    //            => colorLayout=PerFaceVerticle
    //              -> (Ac, Bc, Cc)
    private float[] vertices;
    private float[] normals;
    private float[] colors;
    private int[] faces;
    private ShaderProgram shader;
    private Texture texture;

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

    public enum ModelLayout {
        Absent(0),
        VerticleBound(0),
        PerFaceVerticle(1),
        PerFace(3);
        public final int len;

        private ModelLayout(int len) {
            this.len = len;
        }
    };
    private ModelLayout normalLayout;
    private ModelLayout colorLayout;

    public Model() {
        vertices = normals = colors = new float[]{};
        faces = new int[]{};
        normalLayout = colorLayout = ModelLayout.Absent;
    }

    public Model(float[] vertexes, int[] triangles) {
        this.vertices = vertexes;
        this.faces = triangles;
        normals = colors = new float[]{};
        normalLayout = colorLayout = ModelLayout.Absent;
    }

    public Model(float[] vertices, float[] normals, float[] colors,
            int[] faces, ModelLayout normalLayout, ModelLayout colorLayout) {
        this.vertices = vertices;
        this.faces = faces;
        this.normals = normals;
        this.colors = colors;
        this.normalLayout = normalLayout;
        this.colorLayout = colorLayout;
    }

    public void render(GL gl) {
        int vidx /* Verticle index */ = 0,
                nidx /* Normal index */ = 0,
                cidx /* Color index */ = 0,
                flen /* Face data lengths */ =
                3 + normalLayout.len + colorLayout.len;
        boolean first = false;

        if(texture != null) {
            texture.enable();
            texture.bind();
        }

        if (shader != null) {
            shader.use(gl);
        } else {
            ShaderProgram.unuseCurrent(gl);
        }

        gl.glBegin(gl.GL_TRIANGLES);
        for (int i = 0; i < faces.length; i += flen) {
            for (int voff = i; voff < i + 3; voff++) {
                first = (voff == i);
                vidx = 3 * faces[voff];

                // Normals
                if (normalLayout == ModelLayout.VerticleBound
                        && vidx < normals.length) {
                    nidx = vidx;
                } else {
                    if (colorLayout == ModelLayout.PerFace
                            && first) {
                        nidx = faces[i + 3];
                    } else {
                        if (normalLayout == ModelLayout.PerFaceVerticle) {
                            nidx = faces[voff + 3];
                        }
                    }
                }

                // Colors
                if (colorLayout == ModelLayout.VerticleBound
                        && vidx < colors.length) {
                    cidx = vidx;
                } else {
                    if (colorLayout == ModelLayout.PerFace
                            && first) {
                        cidx = faces[i + 3 + normalLayout.len];
                    } else {
                        if (colorLayout == ModelLayout.PerFaceVerticle) {
                            cidx = faces[voff + 3 + normalLayout.len];
                        }
                    }
                }

                if (normalLayout != ModelLayout.Absent) {
                    if (first || normalLayout != ModelLayout.PerFace) {
                        gl.glNormal3f(normals[nidx], normals[nidx + 1], normals[nidx + 2]);

                        if(texture != null) {
                            gl.glTexCoord2f(normals[nidx], normals[nidx + 1]);
                        }
                    }
                }

                if (colorLayout != ModelLayout.Absent) {
                    if (first || colorLayout != ModelLayout.PerFace) {
                        gl.glColor3f(colors[cidx], colors[cidx + 1], colors[cidx + 2]);
                    }
                }

                gl.glVertex3f(vertices[vidx], vertices[vidx + 1], vertices[vidx + 2]);
            }
        }
        gl.glEnd();

        if(texture != null) {
            texture.disable();
        }
    }
}
