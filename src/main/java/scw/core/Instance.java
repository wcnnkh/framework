package scw.core;

public interface Instance {
	<T> T create();

	<T> T create(Object... params);
}
