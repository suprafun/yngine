package sk.yin.yngine.render.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import sk.yin.yngine.util.Log;

/**
 * ShaderProgramBuilder is used to build up ShaderProgram instances from shader
 * sources.
 *
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class ShaderProgramBuilder {
    private List<String> vertexShaderSources = new ArrayList<String>();
    private List<String> fragmentShaderSources = new ArrayList<String>();
    private static final int NO_SHADER_PROGRAM = -1;

    /**
     * Enumeration for vertex and fragment shaders.
     */
    public enum ShaderType {
        VERTEX(GL.GL_VERTEX_SHADER_ARB),
        FRAGMENT(GL.GL_FRAGMENT_SHADER_ARB);
        public final int glShaderTypeARB;

        private ShaderType(int glShaderTypeARB) {
            this.glShaderTypeARB = glShaderTypeARB;
        }
    };

    /**
     * Constructs an empty ShaderProgramBuilder.
     */
    public ShaderProgramBuilder() {
    }

    /**
     * Adds the shader program code in <code>source</code> to shader sources
     * categorized by value of <code>type</code>.
     * @param type Vertex, or Fragment shader.
     * @param source Shader source code.
     * @return Return value of the collection operation (true if successful), or
     *      false, if couldn't determine destination collection.
     */
    public boolean addShaderSource(ShaderType type, String source) {
        switch (type) {
            case VERTEX:
                return vertexShaderSources.add(source);
            case FRAGMENT:
                return fragmentShaderSources.add(source);
            default:
                return false;
        }
    }

    public ShaderProgram buildShaderProgram(GL gl) {
        int program = gl.glCreateProgramObjectARB(),
                vs[] = compileSourceList(gl, vertexShaderSources, ShaderType.VERTEX, program),
                fs[] = compileSourceList(gl, fragmentShaderSources, ShaderType.FRAGMENT, program);
        gl.glLinkProgramARB(program);
        printBuildInfoLog(gl, program, true);

        return new ShaderProgram(program, vs, fs);
    }

    protected int[] compileSourceList(GL gl, List<String> sources, ShaderType type,
            int program) {
        int shaders[] = new int[sources.size()];
        for (int i = 0, l = sources.size(); i < l; i++) {
            String source = sources.get(i);
            shaders[i] = gl.glCreateShaderObjectARB(type.glShaderTypeARB);
            gl.glShaderSource(shaders[i], 1, new String[]{source}, null);
            gl.glCompileShader(shaders[i]);
            printBuildInfoLog(gl, shaders[i], false);
            if(program != NO_SHADER_PROGRAM)
                gl.glAttachShader(program, shaders[i]);
        }
        return shaders;
    }

    protected void printBuildInfoLog(GL gl, int obj, boolean program) {
        IntBuffer l = IntBuffer.allocate(1),
                n = IntBuffer.allocate(1);
        int len = 0;

        gl.glGetProgramiv(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, l);
        len = l.get(0);
        if (len > 0) {
            ByteBuffer log = ByteBuffer.allocate(len);
            if(program) {
                gl.glGetProgramInfoLog(obj, len, n, log);
            } else {
                gl.glGetProgramInfoLog(obj, len, n, log);
            }

            byte[] ary = new byte[log.remaining()];
            log.get(ary);
            Log.log("Shader compile/link log #" + obj + " len(" + ary.length + "): " + new String(ary));
        }
    }
}
