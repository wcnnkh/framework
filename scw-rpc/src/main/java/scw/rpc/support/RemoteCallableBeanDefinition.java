package scw.rpc.support;

import scw.aop.MethodInterceptor;
import scw.aop.Proxy;
import scw.aop.support.ProxyUtils;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.instance.InstanceException;
import scw.rpc.CallableFactory;

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
