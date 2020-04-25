package scw.aop;

import java.lang.reflect.Method;

import scw.util.attribute.SimpleAttributes;

public class ProxyContext extends SimpleAttributes<Object, Object> {
	private static final long serialVersionUID = 1L;
	private final Object proxy;
	private final Class<?> targetClass;
	private final Method method;
	private final Object[] args;
	private final ProxyContext parentContext;

	public ProxyContext(Object proxy, Class<?> targetClass, Method method,
			Object[] args, ProxyContext parentContext) {
		this.proxy = proxy;
		this.targetClass = targetClass;
		this.method = method;
		this.args = args;
		this.parentContext = parentContext;
	}

	public ProxyContext(Object[] args, ProxyContext context) {
		this(context.getProxy(), context.getTargetClass(), context.getMethod(),
				args, context);
	}

	public Object getProxy() {
		return proxy;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public ProxyContext getParentContext() {
		return parentContext;
	}

	@Override
	public String toString() {
		return method.toString();
	}
}
