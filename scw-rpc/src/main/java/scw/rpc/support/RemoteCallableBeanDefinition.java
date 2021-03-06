package scw.rpc.support;

import scw.aop.MethodInterceptor;
import scw.aop.Proxy;
import scw.beans.BeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;
import scw.rpc.CallableFactory;

public class RemoteCallableBeanDefinition extends DefaultBeanDefinition{
	private final CallableFactory callableFactory;
	
	public RemoteCallableBeanDefinition(BeanFactory beanFactory, CallableFactory callableFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
		this.callableFactory = callableFactory;
	}

	@Override
	public Object create() throws InstanceException {
		MethodInterceptor interceptor = new RemoteMethodInterceptor(callableFactory);
		Proxy proxy = beanFactory.getEnvironment().getProxy(getTargetClass(), null, interceptor);
		Object reference = proxy.create();
		return reference;
	}
}
