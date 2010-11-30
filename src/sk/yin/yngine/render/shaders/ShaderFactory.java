package sk.yin.yngine.render.shaders;

import java.io.IOException;
import java.net.URL;
import javax.media.opengl.GL;
import sk.yin.yngine.render.shaders.ShaderProgramBuilder.ShaderType;
import sk.yin.yngine.resources.ResourceGetter;
import sk.yin.yngine.util.FileUtil;
import sk.yin.yngine.util.Log;

/**
 * Singleton used to create and manage Shader Programs.
 * @author yin
 */
public class ShaderFactory {
    private static final String DEFAULT_VERTEX_SHADER = "default.vert.oglsl";
    private static final String DEFAULT_FRAGMENT_SHADER = "default.frag.oglsl";
    private static ShaderFactory instance;

    private ShaderFactory() {
    }

    /**
     * Returns singleton.
     * @return instance
     */
    public static ShaderFactory getInstance() {
        if (instance == null) {
            instance = new ShaderFactory();
        }
        return instance;
    }

    /**
     * Creates, compiles and links the "default" shader program.
     * @param gl OpenGL context
     * @return ShaderProgram instance
     */
    public ShaderProgram createShaderProgram(GL gl) {
        ShaderProgramBuilder shaderBuilder = new ShaderProgramBuilder();
        FileUtil fileutil = FileUtil.getInstance();
        URL url = null;
        String source;

        url = ResourceGetter.getResource(DEFAULT_VERTEX_SHADER);
        source = fileutil.read(url);
        Log.log("Shader source:\n" + source);
        shaderBuilder.addShaderSource(ShaderType.VERTEX, source);

        url = ResourceGetter.getResource(DEFAULT_FRAGMENT_SHADER);
        source = fileutil.read(url);
        Log.log("Shader source:\n" + source);
        shaderBuilder.addShaderSource(ShaderType.FRAGMENT, source);

        Log.log("Default shader program loaded, running build...");

        return shaderBuilder.buildShaderProgram(gl);
    }
}
