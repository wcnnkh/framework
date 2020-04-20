package scw.core.instance;

public interface InstanceBuilder<T> {
	Class<? extends T> getTargetClass();
	
	T create() throws Exception;

	T create(Object... params) throws Exception;

	T create(Class<?>[] parameterTypes, Object... params) throws Exception;
}
