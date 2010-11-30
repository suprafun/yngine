package sk.yin.yngine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Utility class for file operations. Caches file contents using WeakHashMap.
 * @author Matej 'Yin' Gagyi (matej.gagyi@gmail.com)
 */
public class FileUtil {
    private static FileUtil instance;
    // TODO(mgagyi): Use some ready cache implementation, this one is not right.
    private Map<URL, String> cache = new WeakHashMap();

    /**
     * @return FileUtil instance
     */
    public static FileUtil getInstance() {
        if (instance == null) {
            instance = new FileUtil();
        }
        return instance;
    }

    private boolean isInCache(URL url) {
        return cache.containsKey(url);
    }

    private String getFromCache(URL url) {
        return cache.get(url);
    }

    /**
     * Reads a file from URL and returns it's content as String.
     *
     * @param url File location.
     * @return File contents.
     * @throws IOException
     */
    public String read(URL url) {
        if (isInCache(url)) {
            return getFromCache(url);
        }

        InputStream in = null;
        InputStreamReader inReader = null;
        BufferedReader bufReader = null;
        try {
            in = url.openStream();
            inReader = new InputStreamReader(in);
            bufReader = new BufferedReader(inReader);
            StringBuilder sb = new StringBuilder(1024);
            char[] buf = new char[1024];
            int readNum;

            while ((readNum = bufReader.read(buf)) != -1) {
                sb.append(buf, 0, readNum);
            }

            return sb.toString();
        } catch (IOException ex) {
        } catch (NullPointerException ex) {
        } finally {
            close(in);
            close(inReader);
            close(bufReader);
        }
        return null;
    }

    private void close(InputStream in) {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
        }
    }

    private void close(Reader reader) {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
        }
    }
}
