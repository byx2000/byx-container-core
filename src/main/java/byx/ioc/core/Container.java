package byx.ioc.core;

import java.util.Set;

/**
 * IOC容器
 *
 * @author byx
 */
public interface Container {
    /**
     * 注册对象
     * @param id id
     * @param definition 对象定义
     */
    void registerObject(String id, ObjectDefinition definition);

    /**
     * 获取对象定义
     * @param id id
     * @return 对象定义
     */
    ObjectDefinition getObjectDefinition(String id);

    /**
     * 设置对象定义
     * @param id id
     * @param definition 对象定义
     */
    void setObjectDefinition(String id, ObjectDefinition definition);

    /**
     * 获取指定id的对象
     * @param id id
     * @param <T> 对象类型
     * @return 对象实例
     */
    <T> T getObject(String id);

    /**
     * 获取指定类型的对象
     * @param type 对象类型
     * @param <T> 对象类型
     * @return 对象实例
     */
    <T> T getObject(Class<T> type);

    /**
     * 获取指定id和类型的对象
     * @param id id
     * @param type 对象类型
     * @param <T> 对象类型
     * @return 对象实例
     */
    <T> T getObject(String id, Class<T> type);

    /**
     * 获取指定类型的所有对象
     * @param type 对象类型
     * @param <T> 对象类型
     * @return 对象集合
     */
    <T> Set<T> getObjects(Class<T> type);

    /**
     * 获取容器中所有对象id的集合
     * @return id集合
     */
    Set<String> getObjectIds();

    /**
     * 获取容器中对象类型的集合
     * @return 类型集合
     */
    Set<Class<?>> getObjectTypes();
}
