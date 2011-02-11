package sk.yin.yngine.render.shaders.json;

import java.util.List;

/**
 * 
 * @author Yin
 */
public interface ShaderDefinition {
    public String getVersion();
    public String getDisplay();
    public List<String> getDeps();
}

interface WrittableShaderDefinition {
    public void setVersion(String version);
    public void setDisplay(String display);
    public void setDeps(List<String> deps);
}
