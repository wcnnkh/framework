package run.soeasy.framework.core.concurrent;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class Synchronized implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private final Object mutex; // Object on which to synchronize
	@NonNull
	private final Object source;

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		synchronized (mutex) {
			return method.invoke(source, args);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T proxy(T source, Object mutex) {
		return (T) Proxy.newProxyInstance(source.getClass().getClassLoader(), source.getClass().getInterfaces(),
				new Synchronized(source, mutex));
	}

}
