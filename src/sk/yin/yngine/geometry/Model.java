package sk.yin.yngine.geometry;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import sk.yin.yngine.render.shaders.ShaderProgram;
import sk.yin.yngine.render.shaders.ShaderProgram.ShaderProgramInterface;
import sk.yin.yngine.render.textures.PlainVanilaTexture;

import com.jogamp.opengl.util.texture.Texture;

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
	private PlainVanilaTexture texture;

	public Model() {
		vertices = new float[] {};
		faces = new int[] {};
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

	public void render(GL2 gl) {
		boolean hasNormals = normals != null;
		boolean hasColors = colors != null;
		boolean hasTexCoords = texCoords != null;
		int flen = getFaceLen();
		ShaderProgramInterface iface = null;

		if (shader != null) {
			iface = shader.use(gl);
		} else {
			ShaderProgram.unuseCurrent(gl);
		}

		if (texture != null && hasTexCoords) {
			texture.bind(gl);
			if (iface != null) {
				iface.uniform(gl, "TexUnit0", 0);
				iface.uniform(gl, "texFunc0", 1 /* TEX_MODULATE */);
				iface.uniform(gl, "texEnable0", 1);
			}
		} else {
			if (iface != null)
				iface.uniform(gl, "texEnable0", false);

			gl.glDisable(GL2.GL_TEXTURE_2D);
		}

		if (!hasNormals) {
			gl.glNormal3f(0f, 1f, 0f);
		}
		if (!hasColors) {
			gl.glColor3f(1f, 1f, 1f);
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
				}
				idx = faces[voff];
				gl.glVertex3fv(vertices, idx);
			}
		}
		gl.glEnd();

		if (texture != null) {
			texture.unbind(gl);
		}
	}

	// TODO(yin): Change this and include it in the common rendering pipeline.
	public void renderNormals(GL2 gl) {
		boolean hasNormals = normals != null;
		boolean hasColors = colors != null;
		int flen = getFaceLen();

		if (hasNormals) {
			ShaderProgram.unuseCurrent(gl);
			gl.glDisable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_LIGHTING);
			gl.glDisable(GL2.GL_TEXTURE_2D);
			gl.glEnable(GL2.GL_LINE_SMOOTH);
			gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_FASTEST);
			gl.glEnable(GL2.GL_BLEND);
			gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

			gl.glBegin(GL2.GL_LINES);
			List<Integer> vertxnorm = new ArrayList<Integer>();
			for (int i = 0; i < faces.length; i += flen) {
				for (int voff = i; voff < i + 3; voff++) {
					// TODO(yin): Render all normals for given vertex at once.
					int idxv = faces[voff], // Vertex index
					idxn = faces[voff + 3], // Normal index
					idxc = hasColors ? faces[voff + 6] : -1, // Color index, if
																// present
					lenn = normals.length, id; // Vertex-Normal pair

					if (idxn > -1) {
						id = idxv * lenn + idxn;
						if (vertxnorm.contains(id)) {
							// these are already rendered.
						} else {
							gl.glColor4f(1f, 1f, 1f, 1f);
							if (hasColors && idxc > -1) {
								gl.glColor3f(colors[idxc], colors[idxc + 1],
										colors[idxc + 2]);
							}
							gl.glVertex3fv(vertices, idxv);
							gl.glColor4f(1f, 1f, 1f, 0.5f);
							if (hasColors && idxc > -1) {
								gl.glColor4f(colors[idxc], colors[idxc + 1],
										colors[idxc + 2], 0.5f);
							}
							// TODO(yin): Cache this if normals will be
							// rendered.
							gl.glVertex3f(
									1.5f * normals[idxn] + vertices[idxv], 1.5f
											* normals[idxn + 1]
											+ vertices[idxv + 1], 1.5f
											* normals[idxn + 2]
											+ vertices[idxv + 2]);
							vertxnorm.add(id);
						}
					}
				}
			}
			gl.glEnd();

			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glEnable(GL2.GL_LIGHTING);
			gl.glEnable(GL2.GL_DEPTH_TEST);
			gl.glDisable(GL2.GL_LINE_SMOOTH);
			gl.glDisable(GL2.GL_BLEND);
		}
	}

	public ShaderProgram getShader() {
		return shader;
	}

	public void setShader(ShaderProgram shader) {
		this.shader = shader;
	}

	public PlainVanilaTexture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = new PlainVanilaTexture(texture);
	}

	public void setTexture(PlainVanilaTexture texture) {
		this.texture = texture;
	}

	public float[] colors() {
		return colors;
	}

	private int getFaceLen() {
		return 3 + (normals != null ? 3 : 0) + (colors != null ? 3 : 0)
				+ (texCoords != null ? 3 : 0);
	}

	@Override
	public String toString() {
		return "Model(Fn=" + (faces.length / getFaceLen()) + ",Vn="
				+ (vertices.length / 3)
				+ (colors != null ? ",Cn=" + (colors.length / 3) : "")
				+ (texCoords != null ? ",TCn=" + (texCoords.length / 3) : "")
				+ (texture != null ? ",T0=" + texture.toString() : "")
				+ (shader != null ? ",S=" + shader.toString() : "") + ")";
	}
}
