package byx.ioc.core;

/**
 * 后置包装器：用于对初始化后的对象进行包装操作
 * 在ObjectDefinition的doWrap方法最后回调
 *
 * @author byx
 */
public interface ObjectPostWrapper {
    /**
     * 执行包装操作
     * @param ctx 上下文信息
     * @return 包装后的对象
     */
    Object doWrap(WrapperContext ctx);

    /**
     * 设定包装器执行的顺序，数字小的先执行，数字相同则执行顺序随机
     * @return 顺序值，默认为1
     */
    default int getOrder() {
        return 1;
    }
}
