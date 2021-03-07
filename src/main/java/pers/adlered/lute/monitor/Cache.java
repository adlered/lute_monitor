package pers.adlered.lute.monitor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 缓存控制器，提供键值对的缓存处理
 */
public class Cache {

    private static final LinkedHashMap<String, String> cacheMap = new LinkedHashMap<String, String>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > 200;
        }
    };

    public static String read(String str) throws NullPointerException {
        String result = cacheMap.get(str);
        if (result == null) {
            throw new NullPointerException();
        }
        return result;
    }

    public static void write(String str, String value) {
        cacheMap.put(str, value);
    }
}
