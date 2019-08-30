package scw.core.reflect;

import java.lang.reflect.Type;

public interface PropertyMapper<V> {
	Object mapper(String name, V value, Type type) throws Exception;
}
