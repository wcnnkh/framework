package scw.aop;

public interface Proxy {
	Object create();

	Object create(Class<?>[] parameterTypes, Object[] arguments);
}
