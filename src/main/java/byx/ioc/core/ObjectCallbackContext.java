package byx.ioc.core;

/**
 * 对象回调器上下文
 *
 * @author byx
 */
public class ObjectCallbackContext {
    private final Object obj;
    private final Container container;
    private final ObjectDefinition definition;
    private final String id;

    public ObjectCallbackContext(Object obj, Container container, ObjectDefinition definition, String id) {
        this.obj = obj;
        this.container = container;
        this.definition = definition;
        this.id = id;
    }

    /**
     * 获取原始对象
     */
    public Object getObject() {
        return obj;
    }

    /**
     * 获取注册id
     */
    public String getId() {
        return id;
    }

    /**
     * 获取当前容器
     */
    public Container getContainer() {
        return container;
    }

    /**
     * 获取原始ObjectDefinition
     */
    public ObjectDefinition getDefinition() {
        return definition;
    }
}
