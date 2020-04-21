package scw.aop;

import java.io.Serializable;
import java.lang.reflect.Method;

import scw.core.reflect.MethodHolder;
import scw.lang.NestedExceptionUtils;

public abstract class MethodHolderInvoker extends MethodInvoker implements
		Serializable {
	private static final long serialVersionUID = 1L;
	private final MethodHolder methodHolder;

	public MethodHolderInvoker(MethodHolder methodHolder) {
		this.methodHolder = methodHolder;
	}

	@Override
	public Method getMethod() {
		return methodHolder.getMethod();
	}

	public MethodHolder getMethodHolder() {
		return methodHolder;
	}

	@Override
	public Object invoke(Object... args) throws Throwable {
		try {
			return getMethodHolder().invoke(getInstance(), args);
		} catch (Throwable e) {
			throw NestedExceptionUtils.excludeInvalidNestedExcpetion(e);
		}
	}
}
