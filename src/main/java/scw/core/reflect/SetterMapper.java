package scw.core.reflect;

import java.lang.reflect.Method;

public interface SetterMapper<V> {
	Object mapper(Object bean, Method method, String name, V value, Class<?> type) throws Throwable;
}
