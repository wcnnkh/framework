package scw.core;

public interface InstanceDefinition {
	<T> T create();

	<T> T create(Object... params);

	<T> T create(Class<?>[] parameterTypes, Object... params);
}
