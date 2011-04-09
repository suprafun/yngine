package sk.yin.yngine.render.shaders;

import java.util.HashMap;
import java.util.Map;
import javax.media.opengl.GL;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONValue;
import sk.yin.yngine.util.Log;

/**
 * Represents a shader program objects and holds references to all attaches
 * shader objects. This class also manages global shader disabling/enabling.
 * 
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class ShaderProgram {

    public static final int NO_SHADER_PROGRAM = 0;
    private static int programInUse = NO_SHADER_PROGRAM;
    private static boolean enabled = true;
    private int program;
    private final int[] vertexShaders;
    private final int[] fragmentShaders;
    private final String origin;
    private boolean destroyed = false;
    private ShaderProgramInterface iface = null;

    ShaderProgram(int program, int[] vertexShaders, int[] fragmentShaders, String origin) {
        Log.log("Created shader #" + program
                + "(v: " + join(vertexShaders)
                + "; f: " + join(fragmentShaders) + ")");
        this.program = program;
        this.vertexShaders = vertexShaders;
        this.fragmentShaders = fragmentShaders;
        this.origin = origin;
    }

    public void destroy(GL gl) {
        if (!destroyed) {
            Log.log("Destroying shader #" + program);
            unuse(gl);
            for (int i : vertexShaders) {
                gl.glDetachShader(program, i);
                gl.glDeleteShader(i);
            }
            for (int i : fragmentShaders) {
                gl.glDetachShader(program, i);
                gl.glDeleteShader(i);
            }
            gl.glDeleteProgram(program);
            destroyed = true;
        }
    }

    public ShaderProgramInterface use(GL gl) {
        if (enabled && !destroyed) {
            programInUse = program;
            gl.glUseProgram(program);

            if (iface == null) {
                iface = new ShaderProgramInterfaceImpl();
            }
            return iface;
        }
        return null;
    }

    public void unuse(GL gl) {
        if (programInUse == program) {
            unuseCurrent(gl);
        }
    }

    public static void unuseCurrent(GL gl) {
        programInUse = NO_SHADER_PROGRAM;
        gl.glUseProgram(programInUse);
    }

    public static void enableShaders(GL gl) {
        enabled = true;
    }

    public static void disableShaders(GL gl) {
        enabled = false;
    }

    // TODO(mgagyi): Move to approproate utility class.
    private static String join(int[] ary) {
        return StringUtils.join(ArrayUtils.toObject(ary));
    }

    @Override
    public String toString() {
        return origin;
    }

    public interface ShaderProgramInterface {

        public void uniform(GL gl, String name, Object value);

        public void attribute(GL gl, String name, Object value);

    }

    private class ShaderProgramInterfaceImpl implements ShaderProgramInterface {

        private Map<String, Integer> uniformLocations = new HashMap<String, Integer>();
        private Map<String, Integer> attributeLocations = new HashMap<String, Integer>();

        public void uniform(GL gl, String name, Object value) {
            int loc = getUniformLocation(name, gl);
            if (loc >= 0) {
                if (value instanceof Integer) {
                    setUniform(gl, loc, ((Integer) value).intValue());
                } else if (value instanceof Float) {
                    setUniform(gl, loc, ((Float) value).floatValue());
                } else if (value instanceof Boolean) {
                    setUniform(gl, loc, ((Boolean)value).booleanValue());
                }
            } else {
                Log.log("Shader Program Interface has no uniform: " + name);
            }
        }

        public void attribute(GL gl, String name, Object value) {
            int loc = getUniformLocation(name, gl);
            if (loc >= 0) {
                if (value instanceof Integer) {
                    setAttribute(gl, loc, ((Integer) value).intValue());
                } else if (value instanceof Float) {
                    setAttribute(gl, loc, ((Float) value).floatValue());
                } else if (value instanceof Boolean) {
                    setAttribute(gl, loc, ((Boolean)value).booleanValue());
                }
            } else {
                Log.log("Shader Program Interface has no attribute: " + name);
            }
        }

        private void setUniform(GL gl, int location, int value) {
            gl.glUniform1i(location, value);
        }

        private void setUniform(GL gl, int location, float value) {
            gl.glUniform1f(location, value);
        }

        // TODO(yin): Find how uniform bools are set.
        public void setUniform(GL gl, int location, boolean value) {
            setUniform(gl, location, value ? 1 : 0);
        }

        private int getUniformLocation(String name, GL gl) {
            if (uniformLocations.containsKey(name)) {
                return uniformLocations.get(name);
            } else {
                int ret = gl.glGetUniformLocation(program, name);
                uniformLocations.put(name, ret);
                return ret;
            }
        }

        private void setAttribute(GL gl, int location, int value) {
            gl.glVertexAttrib1s(location, (short) value);
        }

        private void setAttribute(GL gl, int location, float value) {
            gl.glVertexAttrib1f(location, value);
        }

        public void setAttribute(GL gl, int location, boolean value) {
            setAttribute(gl, location, value ? 1 : 0);
        }

        private int getAttributeLocation(String name, GL gl) {
            if (attributeLocations.containsKey(name)) {
                return attributeLocations.get(name);
            } else {
                int ret = gl.glGetAttribLocation(program, name);
                attributeLocations.put(name, ret);
                return ret;
            }
        }
    }
}
