package sk.yin.yngine.render.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

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

        VERTEX(GL2.GL_VERTEX_SHADER),
        FRAGMENT(GL2.GL_FRAGMENT_SHADER);
        public final int glShaderType;

        private ShaderType(int glShaderType) {
            this.glShaderType = glShaderType;
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

    public ShaderProgram buildShaderProgram(GL2 gl) {
        int program = gl.glCreateProgramObjectARB(),
                vs[] = compileShaderSources(gl, ShaderType.VERTEX, program),
                fs[] = compileShaderSources(gl, ShaderType.FRAGMENT, program);
        gl.glLinkProgramARB(program);
        printBuildInfoLog(gl, program, BuildStage.LINK, null, "");

        return new ShaderProgram(program, vs, fs, getOrigin());
    }

    protected int[] compileShaderSources(GL2 gl, ShaderType type, int program) {
        List<String> sources = getSources(type), origins = getOrigins(type);
        int shaders[] = new int[sources.size()];
        for (int i = 0, l = sources.size(); i < l; i++) {
            String source = sources.get(i);
            shaders[i] = gl.glCreateShader(type.glShaderType);

            gl.glShaderSource(shaders[i], 1, new String[]{source}, null);
            gl.glCompileShader(shaders[i]);
            printBuildInfoLog(gl, shaders[i], BuildStage.COMPILE, type, origins.get(i));
            if (program != NO_SHADER_PROGRAM) {
                gl.glAttachShader(program, shaders[i]);
            } else {
                Log.log("Shader compiled, but no program to attach it to...");
            }
        }
        return shaders;
    }

    private List<String> getSources(ShaderType type) {
        switch (type) {
            case VERTEX: return vertexShaderSources;
            case FRAGMENT: return fragmentShaderSources;
        }
        return null;
    }

    private List<String> getOrigins(ShaderType type) {
        switch (type) {
            case VERTEX: return vertexShaderOrigins;
            case FRAGMENT: return fragmentShaderOrigins;
        }
        return null;
    }

    protected void printBuildInfoLog(GL2 gl, int obj, BuildStage stage,
            ShaderType shaderType, String origin) {
        IntBuffer l = IntBuffer.allocate(1),
                n = IntBuffer.allocate(1);
        int len = 0;
        String output = null;
        ByteBuffer log = null;

        switch (stage) {
            case COMPILE:
                gl.glGetShaderiv(obj, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, l);
                len = l.get(0);
                if (len > 0) {
                    log = ByteBuffer.allocate(len);
                    gl.glGetShaderInfoLog(obj, len, n, log);
                }
                break;

            case LINK:
                gl.glGetProgramiv(obj, GL2.GL_OBJECT_INFO_LOG_LENGTH_ARB, l);
                len = l.get(0);
                if (len > 0) {
                    log = ByteBuffer.allocate(len);
                    gl.glGetProgramInfoLog(obj, len, n, log);
                }
                break;
        }

        if (len > 0 && log != null) {
            byte[] ary = new byte[log.remaining()];
            log.get(ary);
            output = "len(" + ary.length + "): " + new String(ary);
        }
        output = obj + "("+origin+") " + output;

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
