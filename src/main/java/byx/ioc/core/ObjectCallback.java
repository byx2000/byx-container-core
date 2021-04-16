package byx.ioc.core;

/**
 * 对象回调器，用于扩展容器的功能
 *
 * @author byx
 */
public interface ObjectCallback{
    /**
     * 对象初始化后回调
     * @param ctx 上下文
     */
    default void afterObjectInit(ObjectCallbackContext ctx) {

    }

    /**
     * 对象包装后回调
     * @param ctx 上下文
     * @return 包装后的对象
     */
    default Object afterObjectWrap(ObjectCallbackContext ctx) {
        return ctx.getObject();
    }

    /**
     * 指定回调器执行的顺序，数字小的先执行
     * @return 顺序值
     */
    default int getOrder() {
        return 1;
    }
}
