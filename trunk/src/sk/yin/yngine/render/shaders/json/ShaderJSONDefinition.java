package sk.yin.yngine.render.shaders.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import sk.yin.yngine.util.Log;

/**
 *
 * @author Yin
 */
public class ShaderJSONDefinition implements ShaderDefinition {

    private JSONObject shader;

    public ShaderJSONDefinition(JSONObject shader) {
        this.shader = shader;
    }

    public String version() {
        return (String) shader.get("version");
    }

    public String display() {
        return (String) shader.get("display");
    }

    public List<String> deps() {
        return (List<String>) shader.get("deps");
    }

    public JSONObject defaults(String variableType) {
        JSONObject defaults = (JSONObject) shader.get("defaults");
        if (defaults != null) {
            return (JSONObject) defaults.get(variableType);
        }
        return null;
    }

    public static ShaderDefinition loadShader(URL url) throws IOException {
        JSONParser parser = new JSONParser();
        InputStream in = url.openStream();
        try {
            Object parsed = parser.parse(new InputStreamReader(in));
            if (parsed instanceof JSONObject) {
                JSONObject shader = (JSONObject) parsed;
                return new ShaderJSONDefinition(shader);
            }
        } catch (ParseException ex) {
            Log.log("Unable to parse (" + url.getFile() + ')', ex);
        }
        return null;
    }
}
