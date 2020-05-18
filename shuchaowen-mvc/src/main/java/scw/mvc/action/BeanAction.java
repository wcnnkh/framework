package scw.mvc.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.aop.Invoker;
import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.lang.NotSupportedException;

public abstract class BeanAction extends AbstractAction {
	private final BeanFactory beanFactory;
	private final Invoker invoker;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass, Method method) {
		super(targetClass, method);
		this.beanFactory = beanFactory;
		if (!Modifier.isStatic(method.getModifiers())) {
			BeanDefinition definition = beanFactory.getDefinition(targetClass);
			if (definition == null || !definition.isInstance()) {
				throw new NotSupportedException("action class: " + targetClass.getName());
			}

			if (definition.isSingleton()) {// 如果是单例，先进行预初始化
				beanFactory.getInstance(targetClass);
			}
		}
		this.invoker = beanFactory.getAop().getProxyMethod(beanFactory, targetClass, method, null);
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
