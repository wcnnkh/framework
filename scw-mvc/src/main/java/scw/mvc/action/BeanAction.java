package scw.mvc.action;

import java.lang.reflect.Method;

import scw.beans.BeanFactory;
import scw.core.reflect.MethodInvoker;

public abstract class BeanAction extends AbstractAction {
	private final BeanFactory beanFactory;
	private final MethodInvoker invoker;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass, Method method) {
		super(targetClass, method);
		this.beanFactory = beanFactory;
		this.invoker = beanFactory.getAop().getProxyMethod(beanFactory, targetClass, method);
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public Object invoke(Object... args) throws Throwable {
		return invoker.invoke(args);
	}

	public Object getInstance() {
		return invoker.getInstance();
	}
}
