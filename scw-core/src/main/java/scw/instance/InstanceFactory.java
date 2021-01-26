package scw.instance;


public interface InstanceFactory extends NoArgsInstanceFactory, InstanceDefinitionFactory {
	boolean isInstance(String name, Object... params);
	
	<T> T getInstance(String name, Object... params);
	
	boolean isInstance(Class<?> clazz, Object... params);

	<T> T getInstance(Class<T> clazz, Object... params);
	
	boolean isInstance(String name, Class<?>[] parameterTypes);

	<T> T getInstance(String name, Class<?>[] parameterTypes, Object... params);

	boolean isInstance(Class<?> clazz, Class<?>[] parameterTypes);
	
	<T> T getInstance(Class<T> clazz, Class<?>[] parameterTypes, Object... params);
}
