package scw.core;

public interface InstanceFactory {
	<T> T get(Class<T> type);
	
	<T> T get(Class<T> type, Object ...params);
}
