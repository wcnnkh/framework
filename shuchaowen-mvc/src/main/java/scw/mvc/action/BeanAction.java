package scw.mvc.action;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.aop.Invoker;
import scw.beans.BeanFactory;
import scw.lang.NotSupportedException;

public abstract class BeanAction extends AbstractAction {
	private final BeanFactory beanFactory;
	private final Invoker invoker;

	public BeanAction(BeanFactory beanFactory, Class<?> targetClass, Method method) {
		super(targetClass, method);
		this.beanFactory = beanFactory;
		
		if(Modifier.isStatic(method.getModifiers())){
			this.invoker = beanFactory.getAop().getProxyMethod(targetClass, null, method);
		}else{
			if(!beanFactory.isInstance(targetClass)){
				throw new NotSupportedException("action class: " + targetClass.getName());
			}
			
			if(beanFactory.isSingleton(targetClass)){
				this.invoker = beanFactory.getAop().getProxyMethod(targetClass, beanFactory.getInstance(targetClass), method);
			}else{
				this.invoker = beanFactory.getAop().getProxyMethod(beanFactory, targetClass, method);
			}
		}
	}

	public Invoker getInvoker() {
		return invoker;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}
}
