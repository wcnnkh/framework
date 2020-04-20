package scw.async;

import java.lang.reflect.Method;

import scw.core.reflect.SerializableMethodHolder;

public class DefaultAsyncRunnableMethod extends AsyncRunnableMethod {
	private static final long serialVersionUID = 1L;
	private final SerializableMethodHolder methodHolder;
	private final String beanName;
	private final Object[] args;

	public DefaultAsyncRunnableMethod(SerializableMethodHolder methodHolder,
			String beanName, Object[] args) {
		this.methodHolder = methodHolder;
		this.beanName = beanName;
		this.args = args;
	}

	@Override
	public Method getMethod() {
		return methodHolder.getMethod();
	}

	@Override
	public Object getInstance() {
		return getInstanceFactory().getInstance(beanName);
	}

	public Object[] getArgs() {
		return args;
	}
}
