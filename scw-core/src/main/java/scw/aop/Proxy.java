package scw.aop;

public interface Proxy {
	Class<?> getTargetClass();

	Object create();

	Object create(Class<?>[] parameterTypes, Object[] params);
}
