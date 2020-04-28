package scw.beans.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import scw.beans.BeanFactory;
import scw.util.value.property.PropertyFactory;

public class NoArgumentMethodIocProcessor extends MethodIocProcessor {
	private final Method method;

	public NoArgumentMethodIocProcessor(Method method) {
		this.method = method;
		checkMethod();
	}

	public Object process(Object bean, BeanFactory beanFactory,
			PropertyFactory propertyFactory) throws Exception {
		return noArgumentInvoke(bean);
	}

	public Method getMethod() {
		return method;
	}

	public Object invoke(Object bean) throws Exception {
		return noArgumentInvoke(bean);
	}

	public Object noArgumentInvoke(Object bean) throws Exception {
		return method.invoke(Modifier.isStatic(method.getModifiers()) ? null
				: bean);
	}
}
