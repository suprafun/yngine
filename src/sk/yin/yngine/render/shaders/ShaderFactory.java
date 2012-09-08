package sk.yin.yngine.render.shaders;

import java.io.IOException;
import java.net.URL;
import java.util.Set;

import javax.media.opengl.GL2;

import org.json.simple.JSONObject;

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
     * @param gl GL2 instance
     * @param name Name of the shader to load.
     * @return ShaderProgram instance.
     */
    public ShaderProgram loadShader(GL2 gl, String name) {
        return loadShader(gl, name, true);
    }

    /**
     * Creates, compiles and links a shader program from shader library.
     * @param gl GL2 instance
     * @param name Name of the shader to load.
     * @param defaultValues Use default values, if defined in shader definition.
     * @return ShaderProgram instance.
     */
    public ShaderProgram loadShader(GL2 gl, String name, boolean defaultValues) {
        String path = SHADER_LIB_PATH + '/' + name + ".shader.json";
        Log.log("Loading shader from: " + path);

        try {
            URL url = ResourceGetter.getResource(path);
            ShaderDefinition def = ShaderJSONDefinition.loadShader(url);
            if (def != null) {
                ShaderProgram ret = createShaderProgram(gl, def);
                if (defaultValues) {
                    Log.log("Using default values for shader variables: ");
                    useDefaultValues(gl, ret, def);
                }
                return ret;
            }
        } catch (IOException ex) {
            Log.log("Couldn't load shader named: " + name, ex);
        }
        return null;
    }

    /**
     * Creates, compiles and links shader program sources defined in
     * <code>def</code>.
     * @param gl    GL2 instance.
     * @param def   Definition of the shader.
     * @return ShaderProgram instance
     */
    public ShaderProgram createShaderProgram(GL2 gl, ShaderDefinition def) {
        ShaderProgramBuilder shaderBuilder = new ShaderProgramBuilder();
        String source;

        for (String dep : def.deps()) {
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

    private void useDefaultValues(GL2 gl, ShaderProgram program, ShaderDefinition def) {
        ShaderProgram.ShaderProgramInterface iface = program.use(gl);
        useValues(gl, iface, def.defaults("uniform"), def.defaults("attribute"));
    }

    private void useValues(GL2 gl, ShaderProgram.ShaderProgramInterface iface, JSONObject uniforms, JSONObject attributes) {
        if (uniforms != null) {
            for (String name : (Set<String>) uniforms.keySet()) {
                Object value = uniforms.get(name);
                iface.uniform(gl, name, value);
            }
        }
        if (attributes != null) {
            for (String name : (Set<String>) uniforms.keySet()) {
                Object value = uniforms.get(name);
                iface.attribute(gl, name, value);
            }
        }
    }
}
