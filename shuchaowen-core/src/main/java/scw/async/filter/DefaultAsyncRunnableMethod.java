package scw.async.filter;

import java.lang.reflect.Method;

import scw.core.reflect.SerializableMethod;

public class DefaultAsyncRunnableMethod extends AsyncRunnableMethod {
	private static final long serialVersionUID = 1L;
	private final SerializableMethod serializableMethod;
	private final String beanName;
	private final Object[] args;

	public DefaultAsyncRunnableMethod(SerializableMethod serializableMethod, String beanName, Object[] args) {
		this.serializableMethod = serializableMethod;
		this.beanName = beanName;
		this.args = args;
	}

	@Override
	public Method getMethod() {
		return serializableMethod.getMethod();
	}

	@Override
	public Object getInstance() {
		return getBeanFactory().getInstance(beanName);
	}

	public Object[] getArgs() {
		return args;
	}
}
