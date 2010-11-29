package sk.yin.yngine.render.shaders;

import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class ShaderProgram {
    private static int programInUse = -1;
    private static boolean enabled = true;
    private int program;
    private final int[] vertexShaders;
    private final int[] fragmentShaders;

    // TODO(mgagyi): Make clean-up centralized w/o need of storing GL ref.
    private GL gl;

    ShaderProgram(int program, int[] vertexShaders, int[] fragmentShaders) {
        this.program = program;
        this.vertexShaders = vertexShaders;
        this.fragmentShaders = fragmentShaders;
    }
/*
    @Override
    protected void finalize() throws Throwable {
        //unuse(gl);
        for (int i : vertexShaders) {
            gl.glDetachObjectARB(program, i);
            gl.glDeleteObjectARB(i);
        }
        for (int i : fragmentShaders) {
            gl.glDetachObjectARB(program, i);
            gl.glDeleteObjectARB(i);
        }
        gl.glDeleteObjectARB(program);
        super.finalize();
    }
*/

    public void use(GL gl) {
        if (enabled) {
            programInUse = program;
            gl.glUseProgramObjectARB(program);
        }
    }

    public void unuse(GL gl) {
        if (programInUse == program) {
            unuseCurrent(gl);
        }
    }

    public static void unuseCurrent(GL gl) {
        programInUse = -1;
        gl.glUseProgramObjectARB(GL.GL_ZERO);
    }

    public static void enableShaders(GL gl) {
        enabled = true;
    }

    public static void disableShaders(GL gl) {
        enabled = false;
    }
}
