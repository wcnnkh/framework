package io.basc.framework.rmi.beans;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.BeansException;
import io.basc.framework.factory.support.FactoryBeanDefinition;

public class RmiClientBeanDefinition extends FactoryBeanDefinition {
	private String host;

	public RmiClientBeanDefinition(BeanFactory beanFactory, Class<?> targetClass, String host) {
		super(beanFactory, targetClass);
		this.host = host;
	}

	@Override
	public boolean isInstance() {
		return Remote.class.isAssignableFrom(getTypeDescriptor().getType());
	}

	@Override
	public Object create() throws BeansException {
		String name = "rmi:" + host + "/" + getTypeDescriptor().getType().getName();
		Object instance;
		try {
			instance = Naming.lookup(name);
		} catch (MalformedURLException e) {
			throw new BeansException(name, e);
		} catch (RemoteException e) {
			throw new BeansException(name, e);
		} catch (NotBoundException e) {
			throw new BeansException(name, e);
		}
		return createInstanceProxy(getAop(), instance, getTypeDescriptor().getType(),
				new Class<?>[] { getTypeDescriptor().getType() }).create();
	}
}
