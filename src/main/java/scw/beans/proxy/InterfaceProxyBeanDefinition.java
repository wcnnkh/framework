package scw.beans.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import scw.beans.AbstractInterfaceBeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;

public class InterfaceProxyBeanDefinition extends AbstractInterfaceBeanDefinition {
	private final InvocationHandler invocationHandler;
	private final BeanFactory beanFactory;
	private final String[] filterNames;

	public InterfaceProxyBeanDefinition(BeanFactory beanFactory, Class<?> interfaceClass,
			InvocationHandler invocationHandler, String[] filterNames) {
		super(interfaceClass);
		this.beanFactory = beanFactory;
		this.invocationHandler = invocationHandler;
		this.filterNames = filterNames;
	}

	@SuppressWarnings("unchecked")
	public <T> T create() {
		Object newProxyInstance = Proxy.newProxyInstance(getInterfaceClass().getClassLoader(),
				new Class[] { getInterfaceClass() }, invocationHandler);
		return (T) BeanUtils.proxyInterface(beanFactory, getInterfaceClass(), newProxyInstance, filterNames);
	}

}
