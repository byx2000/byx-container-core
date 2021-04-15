package byx.ioc.core;

import byx.ioc.exception.LoadExtensionException;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

/**
 * 可扩展的容器工厂
 * 该工厂在初始化时会读取当前类路径及其依赖的Jar包下的所有byx-container-extension.properties文件
 * 然后解析该文件，读取所有ObjectPostWrapper的全限定类名
 * 然后加载并保存这些ObjectPostWrapper
 *
 * 在调用create创建容器的过程中，首先会调用子类实现的initContainer方法对容器进行初始化
 * 然后修改容器中所有ObjectDefinition，在doWrap方法中添加对ObjectPostWrapper的回调
 *
 * @author byx
 */
public abstract class ExtendableContainerFactory implements ContainerFactory {
    private static final String EXTENSION_FILE_NAME = "byx-container-extension.properties";
    private static final String KEY_WRAPPER = "wrappers";
    private static final String DELIMITER = ",";

    /**
     * 保存所有ObjectPostWrapper
     */
    private static final List<ObjectPostWrapper> wrappers = new ArrayList<>();

    static {
        // 从Jar文件中加载所有ObjectPostWrapper
        try {
            // 1. 读取每个Jar包中的byx-container-extension.properties文件
            ClassLoader classLoader = ExtendableContainerFactory.class.getClassLoader();
            Enumeration<URL> urls = classLoader.getResources(EXTENSION_FILE_NAME);

            // 2. 解析每个byx-container-extension.properties文件
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                Properties properties = new Properties();
                properties.load(url.openStream());
                String wrapperClassNames = (String) properties.get(KEY_WRAPPER);
                for (String wrapperClassName : wrapperClassNames.split(DELIMITER)) {
                    Class<?> wrapperClass = Class.forName(wrapperClassName);
                    Constructor<?> defaultConstructor = wrapperClass.getDeclaredConstructor();
                    defaultConstructor.setAccessible(true);
                    wrappers.add((ObjectPostWrapper) defaultConstructor.newInstance());
                }
            }
        } catch (Exception e) {
            throw new LoadExtensionException(e);
        }
    }

    /**
     * 初始化容器
     * 该方法由子类实现
     * @param container 容器
     */
    protected abstract void initContainer(Container container);

    /**
     * 创建容器基本流程：
     * 1. 创建容器
     * 2. 调用子类实现的initContainer对容器进行初始化
     * 3. 修改容器中每个ObjectDefinition，加入ObjectPostWrapper回调
     */
    @Override
    public Container create() {
        Container container = new SimpleContainer();
        initContainer(container);
        wrappers.sort(Comparator.comparingInt(ObjectPostWrapper::getOrder));

        for (String id : container.getObjectIds()) {
            ObjectDefinition definition = container.getObjectDefinition(id);
            container.setObjectDefinition(id, new ObjectDefinition() {
                @Override
                public Dependency[] getInstanceDependencies() {
                    return definition.getInstanceDependencies();
                }

                @Override
                public Class<?> getType() {
                    return definition.getType();
                }

                @Override
                public Object getInstance(Object[] params) {
                    return definition.getInstance(params);
                }

                @Override
                public void doInit(Object obj) {
                    definition.doInit(obj);
                }

                @Override
                public Object doWrap(Object obj) {
                    obj = definition.doWrap(obj);
                    for (ObjectPostWrapper processor : wrappers) {
                        obj = processor.doWrap(new WrapperContext(obj, container, definition, id));
                    }
                    return obj;
                }
            });
        }

        return container;
    }
}
