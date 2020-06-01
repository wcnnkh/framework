package scw.mvc.action;

import java.lang.reflect.Method;

import scw.aop.Invoker;
import scw.beans.BeanFactory;

public abstract class BeanAction extends AbstractAction {
	private final BeanFactory beanFactory;
	private final Invoker invoker;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass,
			Method method) {
		super(targetClass, method);
		this.beanFactory = beanFactory;
		this.invoker = beanFactory.getAop().getProxyMethod(beanFactory,
				targetClass, method);
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
