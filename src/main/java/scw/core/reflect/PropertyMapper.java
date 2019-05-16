package scw.core.reflect;

public interface PropertyMapper<V> {
	Object mapper(String name, V value, Class<?> type) throws Exception;
}
