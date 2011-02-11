package sk.yin.yngine.render.shaders;

import java.io.IOException;
import java.net.URL;
import javax.media.opengl.GL;
import sk.yin.yngine.render.shaders.ShaderProgramBuilder.ShaderType;
import sk.yin.yngine.render.shaders.json.ShaderDefinition;
import sk.yin.yngine.render.shaders.json.ShaderJSONDefinition;
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
    private static final String LIB_VERT = "shaders/main.vert";
    private static final String LIB_FRAG = "shaders/main.frag";
    private static final String LIB_FRAG_LIGHT = "shaders/phong_lighting.frag";
    private static final String LIB_FRAG_TEX = "shaders/texturing.frag";
    private static final String SHADER_LIB_PATH = "shaders";
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
     * Creates, compiles and links a shader program from shader library.
     * @param gl GL instance
     * @param name Name of the shader to load.
     * @return ShaderProgram instance.
     */
    public ShaderProgram loadShader(GL gl, String name) {
        // Stupid NetBeans won't let me cut out the .json extension!
        // TODO(yin): Handle the comment abouf file extensions commented above.
        String path = SHADER_LIB_PATH + '/' + name + ".shader.json";
        Log.log("Loading shader from: " + path);

        try {
            URL url = ResourceGetter.getResource(path);
            ShaderDefinition def = ShaderJSONDefinition.loadShader(url);
            if (def != null) {
                return createShaderProgram(gl, def);
            }
        } catch (IOException ex) {
            Log.log("Couldn't load shader named: " + name, ex);
        }
        return null;
    }


    /**
     * Creates, compiles and links shader program sources defined in
     * <code>def</code>.
     * @param gl    GL instance.
     * @param def   Definition of the shader.
     * @return ShaderProgram instance
     */
    public ShaderProgram createShaderProgram(GL gl, ShaderDefinition def) {
        ShaderProgramBuilder shaderBuilder = new ShaderProgramBuilder();
        String source;

        for (String dep : def.getDeps()) {
            if (dep.endsWith(".vert")) {
                source = getShaderSource(dep);
                Log.log("Vertex shader source: " + dep);
                shaderBuilder.addShaderSource(ShaderType.VERTEX, source, dep);
            } else if (dep.endsWith(".frag")) {
                source = getShaderSource(dep);
                Log.log("Fragment shader source: " + dep);
                shaderBuilder.addShaderSource(ShaderType.FRAGMENT, source, dep);
            } else {
                Log.log("Unknown dependency in shader definition (dropping): " + dep);
            }
        }

        Log.log("Shader program loaded, running build...");
        return shaderBuilder.buildShaderProgram(gl);
    }

    /**
     * Determines the URL string to a file in shader library.
     * @param filename File to find.
     * @return URL string.
     */
    private String getShaderSource(String filename) {
        String url = SHADER_LIB_PATH + '/' + filename;
        URL resource = ResourceGetter.getResource(url);
        return FileUtil.getInstance().read(resource);
    }

    /**
     * Creates, compiles and links the "library" shader program.
     * @param gl GL instance
     * @return ShaderProgram instance.
     */
    @Deprecated
    public ShaderProgram createLib(GL gl) {
        String vertexUrl[] = new String[]{LIB_VERT};
        String fragmentUrl[] = new String[]{LIB_FRAG, LIB_FRAG_LIGHT, LIB_FRAG_TEX};
        return createShaderProgram(gl, vertexUrl, fragmentUrl);
    }

    /**
     * Creates, compiles and links the "default" shader program.
     * @param gl OpenGL context
     * @return ShaderProgram instance
     */
    @Deprecated
    public ShaderProgram createShaderProgram(GL gl) {
        String vertexUrl[] = new String[]{DEFAULT_VERTEX_SHADER},
                fragmentUrl[] = new String[]{DEFAULT_FRAGMENT_SHADER};
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
    @Deprecated
    public ShaderProgram createShaderProgram(GL gl, String[] vertexUrls,
            String[] fragmentUrls) {
        ShaderProgramBuilder shaderBuilder = new ShaderProgramBuilder();
        FileUtil fileutil = FileUtil.getInstance();
        String source;

        for (String url : vertexUrls) {
            URL resource = ResourceGetter.getResource(url);
            source = fileutil.read(resource);
            Log.log("Shader source (vert): " + url);
            shaderBuilder.addShaderSource(ShaderType.VERTEX, source, url);
        }

        for (String url : fragmentUrls) {
            URL resource = ResourceGetter.getResource(url);
            source = fileutil.read(resource);
            Log.log("Shader source (frag): " + url);
            shaderBuilder.addShaderSource(ShaderType.FRAGMENT, source, url);
        }

        Log.log("Default shader program loaded, running build...");
        return shaderBuilder.buildShaderProgram(gl);
    }
}
