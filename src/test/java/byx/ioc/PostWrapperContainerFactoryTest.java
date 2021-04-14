package byx.ioc;

import byx.ioc.core.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PostWrapperContainerFactoryTest {
    private static class MyContainerFactory implements ContainerFactory {
        @Override
        public Container create() {
            Container container = new SimpleContainer();

            container.registerObject("a", new ObjectDefinition() {
                @Override
                public Class<?> getType() {
                    return Integer.class;
                }

                @Override
                public Object getInstance(Object[] params) {
                    return 123;
                }
            });

            container.registerObject("b", new ObjectDefinition() {
                @Override
                public Class<?> getType() {
                    return String.class;
                }

                @Override
                public Object getInstance(Object[] params) {
                    return "hello";
                }
            });

            return container;
        }
    }

    @Test
    public void test() {
        PostWrapperContainerFactory factory = new PostWrapperContainerFactory(new MyContainerFactory());

        factory.addWrapper(ctx -> {
            String id = ctx.getId();
            Object obj = ctx.getObject();
            System.out.println("wrapper 1: id = " + id);
            if (obj instanceof Integer) {
                return (Integer) obj + 1;
            }
            return obj;
        });

        factory.addWrapper(ctx -> {
            String id = ctx.getId();
            Object obj = ctx.getObject();
            System.out.println("wrapper 2: id = " + id);
            if (obj instanceof String) {
                return obj + " hi";
            }
            return obj;
        });

        Container container = factory.create();

        assertEquals(124, container.getObject(Integer.class));
        assertEquals("hello hi", container.getObject(String.class));
    }
}
