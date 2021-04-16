package byx.ioc.core;

import byx.ioc.exception.LoadExtensionException;
import byx.ioc.util.JarUtils;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.*;

/**
 * 可扩展的容器工厂
 * 该工厂在初始化时会读取当前类路径及其依赖的Jar包下的所有byx-container-extension.properties文件
 * 然后解析该文件，读取所有ContainerCallback和ObjectCallback的全限定类名
 * 然后加载并保存这些ContainerCallback和ObjectCallback
 *
 * 在调用create创建容器的过程中，首先会调用子类实现的initContainer方法对容器进行初始化
 * 然后修改容器中所有ObjectDefinition，并在适当的位置调用ContainerCallback和ObjectCallback中的回调方法
 *
 * @author byx
 */
public abstract class ExtendableContainerFactory implements ContainerFactory {
    private static final String EXTENSION_FILE_NAME = "byx-container-extension.properties";
    private static final String KEY_CONTAINER_CALLBACK = "containerCallback";
    private static final String KEY_OBJECT_CALLBACK = "objectCallback";
    private static final String DELIMITER = ",";

    /**
     * 保存所有ContainerCallback
     */
    private static final List<ContainerCallback> containerCallbacks = new ArrayList<>();

    /**
     * 保存所有ObjectCallback
     */
    private static final List<ObjectCallback> objectCallbacks = new ArrayList<>();

    /**
     * 从所有Jar中加载ContainerCallback
     */
    private static void loadContainerCallbacks() {
        try {
            List<URL> urls = JarUtils.getJarResources(EXTENSION_FILE_NAME);
            for (URL url : urls) {
                Properties properties = new Properties();
                properties.load(url.openStream());
                String containerCallbackClassNames = (String) properties.get(KEY_CONTAINER_CALLBACK);
                if (containerCallbackClassNames != null) {
                    for (String containerCallbackClassName : containerCallbackClassNames.split(DELIMITER)) {
                        Class<?> containerCallbackClass = Class.forName(containerCallbackClassName);
                        Constructor<?> defaultConstructor = containerCallbackClass.getDeclaredConstructor();
                        defaultConstructor.setAccessible(true);
                        containerCallbacks.add((ContainerCallback) defaultConstructor.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            throw new LoadExtensionException(e);
        }
    }

    /**
     * 从所有Jar中加载ObjectCallback
     */
    private static void loadObjectCallbacks() {
        try {
            List<URL> urls = JarUtils.getJarResources(EXTENSION_FILE_NAME);
            for (URL url : urls) {
                Properties properties = new Properties();
                properties.load(url.openStream());
                String objectCallbackClassNames = (String) properties.get(KEY_OBJECT_CALLBACK);
                if (objectCallbackClassNames != null) {
                    for (String objectCallbackClassName : objectCallbackClassNames.split(DELIMITER)) {
                        Class<?> objectCallbackClass = Class.forName(objectCallbackClassName);
                        Constructor<?> defaultConstructor = objectCallbackClass.getDeclaredConstructor();
                        defaultConstructor.setAccessible(true);
                        objectCallbacks.add((ObjectCallback) defaultConstructor.newInstance());
                    }
                }
            }
        } catch (Exception e) {
            throw new LoadExtensionException(e);
        }
    }

    static {
        // 加载所有ContainerCallback和ObjectCallback并排序
        loadContainerCallbacks();
        loadObjectCallbacks();
        containerCallbacks.sort(Comparator.comparingInt(ContainerCallback::getOrder));
        objectCallbacks.sort(Comparator.comparingInt(ObjectCallback::getOrder));
    }

    /**
     * 初始化容器
     * 该方法由子类实现
     * @param container 容器
     */
    protected abstract void initContainer(Container container);

    @Override
    public Container create() {
        Container container = new SimpleContainer();
        initContainer(container);

        // 回调所有ContainerCallback
        for (ContainerCallback cc : containerCallbacks) {
            cc.afterContainerInit(container);
        }

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

                    // 回调所有ObjectCallback的afterObjectInit方法
                    for (ObjectCallback oc : objectCallbacks) {
                        oc.afterObjectInit(new ObjectCallbackContext(obj, container, definition, id));
                    }
                }

                @Override
                public Object doWrap(Object obj) {
                    obj = definition.doWrap(obj);

                    // 回调所有ObjectCallback的afterObjectWrap方法
                    for (ObjectCallback oc : objectCallbacks) {
                        obj = oc.afterObjectWrap(new ObjectCallbackContext(obj, container, definition, id));
                    }

                    return obj;
                }
            });
        }

        return container;
    }
}
