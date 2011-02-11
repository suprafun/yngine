package sk.yin.yngine.render.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;
import org.apache.commons.lang.StringUtils;
import sk.yin.yngine.util.Log;

/**
 * ShaderProgramBuilder is used to build up ShaderProgram instances from shader
 * sources.
 *
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class ShaderProgramBuilder {

    private List<String> vertexShaderSources = new ArrayList<String>();
    private List<String> vertexShaderOrigins = new ArrayList<String>();
    private List<String> fragmentShaderSources = new ArrayList<String>();
    private List<String> fragmentShaderOrigins = new ArrayList<String>();
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

    public enum BuildStage {

        COMPILE,
        LINK;
    }

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
    public boolean addShaderSource(ShaderType type, String source, String origin) {
        switch (type) {
            case VERTEX:
                vertexShaderOrigins.add(origin);
                return vertexShaderSources.add(source);
            case FRAGMENT:
                fragmentShaderOrigins.add(origin);
                return fragmentShaderSources.add(source);
            default:
                return false;
        }
    }

    public String getOrigin() {
        return (new StringBuilder().append("VS:[").append(StringUtils.join(vertexShaderOrigins, ",")).append("]-").append("FS:[").append(StringUtils.join(fragmentShaderOrigins, ",")).append("]")).toString();
    }

    public ShaderProgram buildShaderProgram(GL gl) {
        int program = gl.glCreateProgramObjectARB(),
                vs[] = compileSourceList(gl, vertexShaderSources, ShaderType.VERTEX, program),
                fs[] = compileSourceList(gl, fragmentShaderSources, ShaderType.FRAGMENT, program);
        gl.glLinkProgramARB(program);
        printBuildInfoLog(gl, program, BuildStage.LINK, null);

        return new ShaderProgram(program, vs, fs, getOrigin());
    }

    protected int[] compileSourceList(GL gl, List<String> sources, ShaderType type,
            int program) {
        int shaders[] = new int[sources.size()];
        for (int i = 0, l = sources.size(); i < l; i++) {
            String source = sources.get(i);
            shaders[i] = gl.glCreateShaderObjectARB(type.glShaderTypeARB);
                    Log.log("S"+shaders[i]+" "+type.toString()+": "+source.substring(0, 150));

            gl.glShaderSource(shaders[i], 1, new String[]{source}, null);
            gl.glCompileShader(shaders[i]);
            printBuildInfoLog(gl, shaders[i], BuildStage.COMPILE, type);
            if (program != NO_SHADER_PROGRAM) {
                gl.glAttachShader(program, shaders[i]);
            } else {
                Log.log("Shader compiled, but no program to attach it to...");
            }
        }
        return shaders;
    }

    protected void printBuildInfoLog(GL gl, int obj, BuildStage stage, ShaderType shaderType) {
        IntBuffer l = IntBuffer.allocate(1),
                n = IntBuffer.allocate(1);
        int len = 0;
        String output = null;

        gl.glGetProgramiv(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, l);
        len = l.get(0);
        if (len > 0) {
            ByteBuffer log = ByteBuffer.allocate(len);
            switch (stage) {
                case COMPILE:
                    gl.glGetShaderInfoLog(obj, len, n, log);
                    break;
                case LINK:
                    gl.glGetProgramInfoLog(obj, len, n, log);
                    break;
            }

            byte[] ary = new byte[log.remaining()];
            log.get(ary);
            output = "len(" + ary.length + "): " + new String(ary);
        }
        output = obj + " " + output;

        switch (stage) {
            case COMPILE:
                switch (shaderType) {
                    case FRAGMENT:
                        Log.log("Fragment shader compile log #" + output);
                        break;
                    case VERTEX:
                        Log.log("Vertex shader compile log #" + output);
                        break;
                }
                break;
            case LINK:
                Log.log("Shader program link log #" + output);
                break;
        }
    }
}
