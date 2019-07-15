package scw.core.instance;

public interface InstanceFactory extends NoArgsInstanceFactory {
	<T> T getInstance(String name, Object... params);

	<T> T getInstance(Class<T> type, Object... params);

	<T> T getInstance(String name, Class<?>[] parameterTypes, Object... params);

	<T> T getInstance(Class<T> type, Class<?>[] parameterTypes, Object... params);
}
