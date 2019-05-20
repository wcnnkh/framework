package scw.core;

public interface Instance {
	<T> T create();

	<T> T create(Object... params);

	<T> T create(Class<?>[] parameterTypes, Object... args);
}
