package sk.yin.yngine.render.shaders.json;

import java.util.List;

import org.json.simple.JSONObject;

/**
 * 
 * @author Yin
 */
public interface ShaderDefinition {
    public String version();
    public String display();
    public List<String> deps();
    public JSONObject defaults(String string);
}

interface MutableShaderDefinition {
    public void version(String version);
    public void display(String display);
    public void deps(List<String> deps);
}
