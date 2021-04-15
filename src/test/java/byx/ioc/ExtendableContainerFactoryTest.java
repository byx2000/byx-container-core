package byx.ioc;

import byx.aop.annotation.After;
import byx.aop.annotation.Before;
import byx.ioc.core.Container;
import byx.ioc.core.ExtendableContainerFactory;
import byx.ioc.core.ObjectDefinition;
import byx.ioc.extension.aop.annotation.AdviceBy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExtendableContainerFactoryTest {
    private static int c1  = 0, c2 = 0;

    private static class Advice {
        @Before
        public void before() {
            c1++;
            System.out.println("before");
        }

        @After
        public void after() {
            c2++;
            System.out.println("after");
        }
    }

    @AdviceBy(Advice.class)
    public static class A {
        public void f() {
            System.out.println("f");
        }

        public void g() {
            System.out.println("g");
        }
    }

    private static class B {
        public void x() {
            System.out.println("x");
        }

        public void y() {
            System.out.println("y");
        }
    }

    private static class MyContainerFactory extends ExtendableContainerFactory {
        @Override
        protected void initContainer(Container container) {
            container.registerObject("advice", new ObjectDefinition() {
                @Override
                public Class<?> getType() {
                    return Advice.class;
                }

                @Override
                public Object getInstance(Object[] params) {
                    return new Advice();
                }
            });

            container.registerObject("a", new ObjectDefinition() {
                @Override
                public Class<?> getType() {
                    return A.class;
                }

                @Override
                public Object getInstance(Object[] params) {
                    return new A();
                }
            });

            container.registerObject("b", new ObjectDefinition() {
                @Override
                public Class<?> getType() {
                    return B.class;
                }

                @Override
                public Object getInstance(Object[] params) {
                    return new B();
                }
            });
        }
    }

    @Test
    public void test() {
        MyContainerFactory factory = new MyContainerFactory();
        Container container = factory.create();

        A a = container.getObject(A.class);
        c1 = c2 = 0;
        a.f();
        assertEquals(1, c1);
        assertEquals(1, c2);
        a.g();
        assertEquals(2, c1);
        assertEquals(2, c2);

        B b = container.getObject(B.class);
        c1 = c2 = 0;
        b.x();
        b.y();
        assertEquals(0, c1);
        assertEquals(0, c2);
    }
}
