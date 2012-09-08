package sk.yin.yngine.scripts.jython;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import sk.yin.yngine.util.Log;

/**
 * Connects Jython and Java types, performs Python script execution
 * and Python type instantiation. In the future, script file change monitoring
 * ans online script reloading should be implemented here.
 *
 * @author Matej 'Yin' Gagyi <matej.gagyi+yngine+src@gmail.com>
 */
public class JythonConnector {

    private static final String SCRIPT_FOLDER = "src/sk/yin/yngine/scripts";
	private String filename;
    private PythonInterpreter python = null;

    public JythonConnector(String filename) {
        this.filename = filename;
    }

    public void set(String name, Object value) {
        python().set(name, value);
    }

    public PyObject get(String name) {
        return python().get(name);
    }

    public Object getTranslate(String name, Class clazz) {
        PyObject obj = get(name);
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        } else {
            return null;
        }
    }

    public void run() {
        try {
            python().execfile(SCRIPT_FOLDER + "/jython/" + filename);
        } catch (Exception ex) {
            // TODO(yin): Add exception handling for script.
            Log.log("Python exception has been caught");
            ex.printStackTrace();
        }
    }

    private PythonInterpreter python() {
        if (python == null) {
            python = new PythonInterpreter();
        }
        return python;

    }
}
