package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

public class MethodInvokerWrapper implements MethodInvoker, Serializable {
	private static final long serialVersionUID = 1L;
	private final MethodInvoker source;

	public MethodInvokerWrapper(MethodInvoker source) {
		this.source = source;
	}

	public Object invoke(Object... args) throws Throwable {
		return source.invoke(args);
	}

	public Object getInstance() {
		return source.getInstance();
	}

	public MethodInvoker getSource() {
		return source;
	}

	@Override
	public String toString() {
		return source.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return source.equals(obj);
	}

	@Override
	public int hashCode() {
		return source.hashCode();
	}

	public Class<?> getSourceClass() {
		return source.getSourceClass();
	}

	public Method getMethod() {
		return source.getMethod();
	}
}
