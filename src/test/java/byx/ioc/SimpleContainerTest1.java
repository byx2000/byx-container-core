package byx.ioc;

import byx.ioc.core.Container;
import byx.ioc.core.Dependency;
import byx.ioc.core.ObjectDefinition;
import byx.ioc.core.SimpleContainer;
import byx.ioc.exception.IdDuplicatedException;
import byx.ioc.exception.IdNotFoundException;
import byx.ioc.exception.MultiTypeMatchException;
import byx.ioc.exception.TypeNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 基本用法
 */
public class SimpleContainerTest1 {
    /**
     * 根据id获取对象、根据类型获取对象、异常
     */
    @Test
    public void test1() {
        ObjectDefinition f1 = new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return "hello";
            }
        };

        ObjectDefinition f2 = new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return Integer.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return 123;
            }
        };

        ObjectDefinition f3 = new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return 3.14;
            }
        };

        ObjectDefinition f4 = new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return Double.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return 6.28;
            }
        };

        Container container = new SimpleContainer();
        container.registerObject("f1", f1);
        container.registerObject("f2", f2);
        assertThrows(IdDuplicatedException.class, () -> container.registerObject("f1", f1));
        container.registerObject("f3", f3);
        container.registerObject("f4", f4);

        String s = container.getObject(String.class);
        assertEquals("hello", s);

        s = container.getObject("f1", String.class);
        assertEquals("hello", s);

        Integer i = container.getObject(Integer.class);
        assertEquals(123, i);

        i = container.getObject("f2", Integer.class);
        assertEquals(123, i);

        String s2 = container.getObject("f1");
        assertSame(s, s2);

        Integer i2 = container.getObject("f2");
        assertSame(i, i2);

        assertThrows(IdNotFoundException.class, () -> container.getObject("f5"));
        assertThrows(TypeNotFoundException.class, () -> container.getObject(Boolean.class));
        assertThrows(MultiTypeMatchException.class, () -> container.getObject(Double.class));
        assertThrows(IdNotFoundException.class, () -> container.getObject("aaa", String.class));
        assertThrows(TypeNotFoundException.class, () -> container.getObject("f1", Integer.class));
    }

    /**
     * 通过父类类型获取子类对象
     */
    @Test
    public void test2() {
        Container container = new SimpleContainer();

        ObjectDefinition f = new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return "hello";
            }
        };

        container.registerObject("f", f);

        CharSequence s = container.getObject(CharSequence.class);
        assertEquals("hello", s);
    }

    /**
     * setObjectDefinition
     */
    @Test
    public void test3() {
        Container container = new SimpleContainer();

        assertThrows(IdNotFoundException.class, () -> {
            container.setObjectDefinition("msg", new ObjectDefinition() {
                @Override
                public Class<?> getType() {
                    return String.class;
                }

                @Override
                public Object getInstance(Object[] params) {
                    return "hello";
                }
            });
        });

        container.registerObject("msg", new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return "aaa";
            }
        });

        container.setObjectDefinition("msg", new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return "bbb";
            }
        });

        assertEquals("bbb", container.getObject("msg"));

        assertNotNull(container.getObjectDefinition("msg"));
        assertThrows(IdNotFoundException.class, () -> container.getObjectDefinition("msg2"));
    }

    /**
     * 异常
     */
    @Test
    public void test4() {
        Container container = new SimpleContainer();

        container.registerObject("a", new ObjectDefinition() {
            @Override
            public Dependency[] getInstanceDependencies() {
                return new Dependency[]{Dependency.type(String.class)};
            }

            @Override
            public Class<?> getType() {
                return Integer.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return 123;
            }
        });

        assertThrows(TypeNotFoundException.class, () -> container.getObject("a"));
    }

    /**
     * 异常
     */
    @Test
    public void test5() {
        Container container = new SimpleContainer();

        container.registerObject("a", new ObjectDefinition() {
            @Override
            public Dependency[] getInstanceDependencies() {
                return new Dependency[]{Dependency.id("x")};
            }

            @Override
            public Class<?> getType() {
                return Integer.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return 123;
            }
        });

        assertThrows(IdNotFoundException.class, () -> container.getObject("a"));
    }

    /**
     * getObjects
     */
    @Test
    public void test6() {
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
                return "abc";
            }
        });

        container.registerObject("c", new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return "def";
            }
        });

        Set<String> s1 = container.getObjects(String.class);
        assertEquals(Set.of("abc", "def"), s1);

        Set<Integer> s2 = container.getObjects(Integer.class);
        assertEquals(Set.of(123), s2);

        Set<Double> s3 = container.getObjects(Double.class);
        assertTrue(s3.isEmpty());
    }

    /**
     * getObjectTypes
     */
    @Test
    public void test7() {
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
                return Integer.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return 456;
            }
        });

        container.registerObject("c", new ObjectDefinition() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public Object getInstance(Object[] params) {
                return "hello";
            }
        });

        Set<Class<?>> types = container.getObjectTypes();
        assertEquals(Set.of(Integer.class, String.class), types);
    }
}
