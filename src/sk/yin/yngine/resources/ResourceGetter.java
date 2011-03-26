package sk.yin.yngine.resources;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author yin
 */
public class ResourceGetter {
    private static ResourceGetter instance;

    private ResourceGetter() {
    }

    public static URL getResource(String name) {
        if (instance == null) {
            instance = new ResourceGetter();
        }
        return instance.getClass().getResource(name);
    }

    public static List<URL> getResources(String names[]) {
        List<URL> urls = new ArrayList<URL>();
        for (String file : names) {
            URL url = getResource(file);
            if (url != null) {
                urls.add(url);
            }
        }
        return urls;
    }

    public static URL getFirstResourcePresent(String names[]) {
        for (String file : names) {
            URL url = getResource(file);
            if (url != null) {
                return url;
            }
        }
        return null;
    }
}
