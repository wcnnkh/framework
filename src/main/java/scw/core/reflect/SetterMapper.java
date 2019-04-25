package scw.core.reflect;

import java.lang.reflect.Method;

public interface SetterMapper {
	Object mapper(Object bean, Method method, String name, String value, Class<?> type) throws Throwable;
}
