package scw.core;

public interface InstanceFactory {
	<T> T get(Class<T> type);
	
	<T> T get(Class<T> type, Object ...params);
	
	<T> T get(Class<T> type, Class<?>[] parameterTypes, Object ...params);
}
