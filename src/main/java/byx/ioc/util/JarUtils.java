package byx.ioc.util;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Jar包工具类
 *
 * @author byx
 */
public class JarUtils {
    /**
     * 获取所有Jar包下的资源
     * @param resource 资源名
     * @return 资源URL列表
     */
    public static List<URL> getJarResources(String resource) {
        try {
            ClassLoader classLoader = JarUtils.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(resource);
            List<URL> result = new ArrayList<>();
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                result.add(url);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
