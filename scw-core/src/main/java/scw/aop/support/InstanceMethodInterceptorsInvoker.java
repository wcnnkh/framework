package scw.aop.support;

import java.util.Iterator;

import scw.aop.MethodInterceptor;
import scw.aop.ProxyInstanceTarget;
import scw.core.reflect.DefaultMethodInvoker;
import scw.core.reflect.MethodInvoker;
import scw.core.utils.ArrayUtils;

public class InstanceMethodInterceptorsInvoker extends MethodInterceptorsInvoker {
	private static final long serialVersionUID = 1L;
	private final Object instance;

	public InstanceMethodInterceptorsInvoker(MethodInvoker source,
			Iterator<MethodInterceptor> iterator, Object instance) {
		super(source, iterator);
		this.instance = instance;
	}
	
	@Override
	public Object intercept(MethodInvoker invoker, Object[] args)
			throws Throwable {
		if(ArrayUtils.isEmpty(args) && invoker.getMethod().getName().equals(ProxyInstanceTarget.PROXY_TARGET_METHOD_NAME)){
			return invoker.getInstance();
		}
		return super.intercept(invoker, args);
	}

	@Override
	public Object invoke(Object... args) throws Throwable {
		return intercept(new DefaultMethodInvoker(instance, getDeclaringClass(), getMethod(), true), args);
	}
}
