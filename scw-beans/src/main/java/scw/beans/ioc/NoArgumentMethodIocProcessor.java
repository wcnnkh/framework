package scw.beans.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.core.reflect.ReflectionUtils;

public class NoArgumentMethodIocProcessor extends AbstractIocProcessor {
	private final Method method;

	public NoArgumentMethodIocProcessor(Method method) {
		this.method = method;
		checkMethod(method);
	}

	public void process(BeanDefinition beanDefinition, Object bean, BeanFactory beanFactory) throws BeansException {
		if(acceptModifiers(beanDefinition, bean, method.getModifiers())){
			noArgumentInvoke(bean);
		}
	}

	public Method getMethod() {
		return method;
	}

	public Object noArgumentInvoke(Object bean) throws BeansException {
		return ReflectionUtils.invokeMethod(method, Modifier.isStatic(method.getModifiers()) ? null
				: bean);
	}
}
