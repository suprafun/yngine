/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sk.yin.yngine.render.lights;

import javax.media.opengl.GL2;

/**
 *
 * @author yin
 */
    public enum MaterialDef {
        Copper(0.3f, 0.7f, 0.6f, 6.0f, 1.8f, 228, 123, 87),
        Rubber(0.3f, 0.7f, 0.0f, 0.0f, 1.00f, 3, 139, 251),
        Brass(0.3f, 0.7f, 0.7f, 8.00f, 2.0f, 228, 187, 34),
        Glass(0.3f, 0.7f, 0.7f, 32.00f, 1.0f, 199, 227, 208),
        Plastic(0.3f, 0.9f, 0.9f, 32.0f, 1.0f, 0, 19, 252),
        Pearl(1.5f, -0.5f, 2.0f, 99.0f, 1.0f, 255, 138, 138),

        Floor(1.0f, 1.0f, 1.0f, 128.0f, 1.0f, 200, 225, 255),
        Full(1.0f, 1.0f, 1.0f, 32.0f, 1.0f, 255, 255, 255),
        Half(0.5f, 0.5f, 0.5f, 16.0f, 1.0f, 255, 255, 255);
        public final float ambient[], diffuse[], specular[], shininess, briliance, c[];
        public static final int GL_FACE = GL2.GL_FRONT;

        MaterialDef(float ambient, float diffuse, float specular, float shinines,
                float brilliance, int r, int g, int b) {
            float rf = (float) r / 255f,
                    gf = (float) g / 255f,
                    bf = (float) b / 255f;
            this.ambient =
                    new float[]{ambient * rf, ambient * gf, ambient * bf, 1.0f};
            this.diffuse =
                    new float[]{diffuse * rf, diffuse * gf, diffuse * bf, 1.0f};
            this.specular =
                    new float[]{specular * rf, specular * gf, specular * bf, 1.0f};
            this.shininess = shinines;
            this.briliance = brilliance;
            c =
                    new float[]{(float) r / 255, (float) g / 255, (float) b / 512, 1.0f};
        }

        public void use(GL2 gl) {
            gl.glMaterialfv(GL_FACE, GL2.GL_AMBIENT, ambient, 0);
            gl.glMaterialfv(GL_FACE, GL2.GL_DIFFUSE, diffuse, 0);
            gl.glMaterialfv(GL_FACE, GL2.GL_SPECULAR, specular, 0);
            gl.glMaterialf(GL_FACE, GL2.GL_SHININESS, shininess);
        }
    }

