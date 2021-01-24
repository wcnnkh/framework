package scw.rmi;

import java.rmi.Naming;
import java.rmi.Remote;

import scw.beans.BeanFactory;
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
	public Object create() throws Exception {
		Object instance = Naming.lookup("rmi:" + host + "/" + getTargetClass().getName());
		return createInstanceProxy(instance, getTargetClass(), new Class<?>[]{getTargetClass()}).create();
	}
}
