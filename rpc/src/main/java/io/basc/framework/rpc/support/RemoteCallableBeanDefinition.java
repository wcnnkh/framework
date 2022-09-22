package io.basc.framework.rpc.support;

import java.util.function.Supplier;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.factory.BeanFactory;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.factory.support.FactoryBeanDefinition;
import io.basc.framework.rpc.CallableFactory;

public class RemoteCallableBeanDefinition extends FactoryBeanDefinition {
	private final Supplier<CallableFactory> callableFactory;

	public RemoteCallableBeanDefinition(BeanFactory beanFactory, Supplier<CallableFactory> callableFactory,
			Class<?> sourceClass) {
		super(beanFactory, sourceClass);
		this.callableFactory = callableFactory;
	}

	@Override
	public boolean isInstance() {
		return true;
	}

	@Override
	public boolean isAopEnable(BeanResolver beanResolver) {
		return true;
	}

	@Override
	public Object create() throws InstanceException {
		MethodInterceptor interceptor = new RemoteMethodInterceptor(callableFactory.get());
		Proxy proxy = ProxyUtils.getFactory().getProxy(getTypeDescriptor().getType(), null, interceptor);
		Object reference = proxy.create();
		return reference;
	}
}
