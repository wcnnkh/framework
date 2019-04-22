package scw.beans;

public interface BeanFactory {
	<T> T get(String name);

	<T> T get(Class<T> type);
	
	<T> T get(String name, Object ...params);
	
	<T> T get(Class<T> type, Object ...params);
	
	<T> T get(String name, Class<?>[] parameterTypes, Object ...params);
	
	<T> T get(Class<T> type, Class<?>[] parameterTypes, Object ...params);
	
	boolean contains(String name);
}
