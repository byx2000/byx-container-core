package byx.ioc.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 带后置包装器扩展功能的容器工厂
 *
 * @author byx
 */
public class PostWrapperContainerFactory implements ContainerFactory {
    private final ContainerFactory containerFactory;

    private final List<ObjectPostWrapper> wrappers = new ArrayList<>();

    /**
     * 创建PostWrapperContainerFactory
     * @param containerFactory 原始容器工厂
     */
    public PostWrapperContainerFactory(ContainerFactory containerFactory) {
        this.containerFactory = containerFactory;
    }

    /**
     * 添加后置包装器
     * @param wrapper 包装器
     */
    public void addWrapper(ObjectPostWrapper wrapper) {
        wrappers.add(wrapper);
    }

    @Override
    public Container create() {
        Container container = containerFactory.create();
        wrappers.sort(Comparator.comparingInt(ObjectPostWrapper::getOrder));

        for (String id : container.getObjectIds()) {
            ObjectDefinition definition = container.getObjectDefinition(id);
            container.setObjectDefinition(id, new ObjectDefinition() {
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
