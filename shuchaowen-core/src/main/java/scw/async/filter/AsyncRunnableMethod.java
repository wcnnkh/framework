package scw.async.filter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.async.AbstractAsyncRunnable;
import scw.core.reflect.ReflectionUtils;

public abstract class AsyncRunnableMethod extends AbstractAsyncRunnable {
	private static final long serialVersionUID = 1L;

	public abstract Object getInstance();

	public abstract Method getMethod();

	public abstract Object[] getArgs();

	public Object call() throws Exception {
		Method method = getMethod();
		ReflectionUtils.setAccessibleMethod(method);
		if (Modifier.isStatic(method.getModifiers())) {
			return method.invoke(null, getArgs());
		} else {
			return method.invoke(getInstance(), getArgs());
		}
	}

	@Override
	public String toString() {
		return getMethod().toString();
	}
}