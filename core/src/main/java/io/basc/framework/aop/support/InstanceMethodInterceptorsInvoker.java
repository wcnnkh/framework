package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.ProxyInstanceTarget;
import io.basc.framework.core.reflect.DefaultMethodInvoker;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.util.ArrayUtils;

import java.util.Iterator;

public class InstanceMethodInterceptorsInvoker extends MethodInterceptorsInvoker {
	private static final long serialVersionUID = 1L;
	private final Object instance;

	public InstanceMethodInterceptorsInvoker(MethodInvoker source, Iterator<MethodInterceptor> iterator,
			Object instance) {
		super(source, iterator);
		this.instance = instance;
	}

	@Override
	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		if (ArrayUtils.isEmpty(args)
				&& invoker.getMethod().getName().equals(ProxyInstanceTarget.PROXY_TARGET_METHOD_NAME)) {
			return invoker.getInstance();
		}
		return super.intercept(invoker, args);
	}

	@Override
	public Object invoke(Object... args) throws Throwable {
		return intercept(new DefaultMethodInvoker(instance, getSourceClass(), getMethod(), true), args);
	}
}
