package io.basc.framework.rpc.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.Proxy;
import io.basc.framework.aop.support.ProxyUtils;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.instance.InstanceException;
import io.basc.framework.rpc.CallableFactory;

public class RemoteCallableBeanDefinition extends DefaultBeanDefinition{
	private final CallableFactory callableFactory;
	
	public RemoteCallableBeanDefinition(ConfigurableBeanFactory beanFactory, CallableFactory callableFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
		this.callableFactory = callableFactory;
	}
	
	@Override
	public boolean isInstance() {
		return true;
	}
	
	@Override
	public boolean isAopEnable() {
		return true;
	}

	@Override
	public Object create() throws InstanceException {
		MethodInterceptor interceptor = new RemoteMethodInterceptor(callableFactory);
		Proxy proxy = ProxyUtils.getFactory().getProxy(getTargetClass(), null, interceptor);
		Object reference = proxy.create();
		return reference;
	}
}
