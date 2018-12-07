package shuchaowen.beans;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import shuchaowen.beans.property.PropertiesFactory;

public class NoArgumentBeanMethod implements BeanMethod{
	private final Method method;
	private final boolean isStaticMethod;
	
	public NoArgumentBeanMethod(Method method){
		this.method = method;
		this.isStaticMethod = Modifier.isStatic(method.getModifiers());
	}

	public Object invoke(Object bean, BeanFactory beanFactory,
			PropertiesFactory propertiesFactory) throws Exception {
		return method.invoke(isStaticMethod? null:bean);
	}

	public Method getMethod() {
		return method;
	}
}
