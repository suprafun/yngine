package sk.yin.yngine.render.shaders;

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
    private static final String LIB_VERT = "./shaders/main.vert";
    private static final String LIB_FRAG = "./shaders/main.frag";
    private static final String LIB_FRAG_LIGHT = "./shaders/phong_lighting.frag";
    private static final String LIB_FRAG_TEX = "./shaders/texturing.frag";
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
     * Creates, compiles and links the "library" shader program.
     * @param gl GL instance
     * @return ShaderProgram instance.
     */
    public ShaderProgram createLib(GL gl) {
        URL vertexUrl[] = new URL[]{
            ResourceGetter.getResource(LIB_VERT)
        };
        URL fragmentUrl[] = new URL[] {
            ResourceGetter.getResource(LIB_FRAG),
            ResourceGetter.getResource(LIB_FRAG_LIGHT),
            ResourceGetter.getResource(LIB_FRAG_TEX)
        };
        return createShaderProgram(gl, vertexUrl, fragmentUrl);
    }

    /**
     * Creates, compiles and links the "default" shader program.
     * @param gl OpenGL context
     * @return ShaderProgram instance
     */
    public ShaderProgram createShaderProgram(GL gl) {
        URL vertexUrl[] = new URL[1],
                fragmentUrl[] = new URL[1];
        vertexUrl[0] = ResourceGetter.getResource(DEFAULT_VERTEX_SHADER);
        fragmentUrl[0] = ResourceGetter.getResource(DEFAULT_FRAGMENT_SHADER);
        return createShaderProgram(gl, vertexUrl, fragmentUrl);
    }

    /**
     * Creates, compiles and links shader program given by <code>vertexSources
     * </code> and fragmentSources.
     * @param gl                GL instance.
     * @param vertexSources     Array of URLs of vertex program.
     * @param fragmentSources   Array of URLs of fragment program.
     * @return ShaderProgram instance
     */
    public ShaderProgram createShaderProgram(GL gl, URL[] vertexSources,
            URL[] fragmentSources) {
        ShaderProgramBuilder shaderBuilder = new ShaderProgramBuilder();
        FileUtil fileutil = FileUtil.getInstance();
        String source;

        for (URL url : vertexSources) {
            source = fileutil.read(url);
            Log.log("Shader source (vert): " + url.toString());
            shaderBuilder.addShaderSource(ShaderType.VERTEX, source);
        }

        for (URL url : fragmentSources) {
            source = fileutil.read(url);
            Log.log("Shader source (frag): " + url.toString());
            shaderBuilder.addShaderSource(ShaderType.FRAGMENT, source);
        }

        Log.log("Default shader program loaded, running build...");
        return shaderBuilder.buildShaderProgram(gl);
    }
}
