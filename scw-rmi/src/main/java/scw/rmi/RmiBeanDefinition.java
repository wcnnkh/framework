package scw.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.support.DefaultBeanDefinition;


public class RmiBeanDefinition extends DefaultBeanDefinition{
	private String host;
	
	public RmiBeanDefinition(BeanFactory beanFactory, Class<?> targetClass, String host) {
		super(beanFactory, targetClass);
		this.host = host;
	}

	@Override
	public boolean isInstance() {
		return Remote.class.isAssignableFrom(getTargetClass());
	}
	
	@Override
	public Object create() throws BeansException {
		String name = "rmi:" + host + "/" + getTargetClass().getName();
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
		return createInstanceProxy(instance, getTargetClass(), new Class<?>[]{getTargetClass()}).create();
	}
}
