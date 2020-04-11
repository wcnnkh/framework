package scw.mvc.action;

import java.lang.reflect.Method;

import scw.aop.Invoker;
import scw.beans.AutoProxyMethodInvoker;
import scw.beans.BeanFactory;

public abstract class BeanAction extends AbstractAction {
	private final Invoker invoker;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass,
			Method method) {
		super(targetClass, method);
		this.invoker = new AutoProxyMethodInvoker(beanFactory, targetClass,
				method);
	}

	public Invoker getInvoker() {
		return invoker;
	}
}
