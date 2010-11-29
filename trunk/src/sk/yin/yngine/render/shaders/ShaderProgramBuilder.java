package sk.yin.yngine.render.shaders;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.media.opengl.GL;

/**
 *
 * @author yin
 */
public class ShaderProgramBuilder {
    private List<String> vertexShaderSources = new ArrayList<String>();
    private List<String> fragmentShaderSources = new ArrayList<String>();
    private static final int NO_SHADER_PROGRAM = -1;

    public enum ShaderType {
        VERTEX(GL.GL_VERTEX_SHADER_ARB),
        FRAGMENT(GL.GL_FRAGMENT_SHADER_ARB);
        public final int glShaderTypeARB;

        private ShaderType(int glShaderTypeARB) {
            this.glShaderTypeARB = glShaderTypeARB;
        }
    };

    public ShaderProgramBuilder() {
    }

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

    protected void printBuildInfoLog(GL gl, int obj) {
        IntBuffer l = IntBuffer.allocate(1),
                n = IntBuffer.allocate(1);
        gl.glGetObjectParameterivARB(obj, GL.GL_OBJECT_INFO_LOG_LENGTH_ARB, l);
        if (l.get(0) > 0) {
            ByteBuffer log = ByteBuffer.allocate(l.get(0));
            gl.glGetInfoLogARB(obj, l.get(0), n, null);
            byte[] ary = new byte[log.remaining()];
            log.get(ary);
            System.out.println("Shader log " + obj + " len(" + ary.length + "): " + new String(ary));
        }
    }

    public ShaderProgram buildShaderProgram(GL gl) {
        int program = gl.glCreateProgramObjectARB(),
                vs[] = compileSourceList(gl, vertexShaderSources, ShaderType.VERTEX, program),
                fs[] = compileSourceList(gl, fragmentShaderSources, ShaderType.FRAGMENT, program);
        gl.glLinkProgramARB(program);
        printBuildInfoLog(gl, program);

        return new ShaderProgram(program, vs, fs);
    }

    protected int[] compileSourceList(GL gl, List<String> sources, ShaderType type,
            int program) {
        int shaders[] = new int[sources.size()];
        for (int i = 0, l = sources.size(); i < l; i++) {
            String source = sources.get(i);
            shaders[i] = gl.glCreateShaderObjectARB(type.glShaderTypeARB);
            gl.glShaderSourceARB(shaders[i], 1, new String[]{source}, null);
            gl.glCompileShaderARB(shaders[i]);
            printBuildInfoLog(gl, shaders[i]);
            if(program != NO_SHADER_PROGRAM)
                gl.glAttachObjectARB(program, shaders[i]);
        }
        return shaders;
    }
}
