package sk.yin.yngine.render.shaders;

import java.util.Arrays;
import javax.media.opengl.GL;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import sk.yin.yngine.util.Log;

/**
 * Represents a shader program objects and holds references to all attaches
 * shader objects. This class also manages.
 * 
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class ShaderProgram {
    public static final int NO_SHADER_PROGRAM = 0;
    private static int programInUse = NO_SHADER_PROGRAM;
    private static boolean enabled = true;
    private int program;
    private final int[] vertexShaders;
    private final int[] fragmentShaders;
    private boolean destroyed;

    ShaderProgram(int program, int[] vertexShaders, int[] fragmentShaders) {
        Log.log("Created shader #" + program
                + "(v: " + join(vertexShaders)
                + "; f: " + join(fragmentShaders) + ")");
        this.program = program;
        this.vertexShaders = vertexShaders;
        this.fragmentShaders = fragmentShaders;
    }

    public void destroy(GL gl) {
        if (!destroyed) {
            Log.log("Destroying shader #" + program);
            unuse(gl);
            for (int i : vertexShaders) {
                gl.glDetachObjectARB(program, i);
                gl.glDeleteObjectARB(i);
            }
            for (int i : fragmentShaders) {
                gl.glDetachObjectARB(program, i);
                gl.glDeleteObjectARB(i);
            }
            gl.glDeleteObjectARB(program);
            destroyed = true;
        }
    }

    public void use(GL gl) {
        if (enabled && !destroyed) {
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
        programInUse = NO_SHADER_PROGRAM;
        gl.glUseProgramObjectARB(programInUse);
    }

    public static void enableShaders(GL gl) {
        enabled = true;
    }

    public static void disableShaders(GL gl) {
        enabled = false;
    }

    // TODO(mgagyi): Move to approproate utility class.
    private static String join(int[] ary) {
        return StringUtils.join(ArrayUtils.toObject(ary));
    }
}
