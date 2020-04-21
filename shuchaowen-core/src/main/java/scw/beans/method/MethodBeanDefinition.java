package scw.beans.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import scw.beans.BeanFactory;
import scw.beans.DefaultBeanDefinition;
import scw.util.value.property.PropertyFactory;

public class MethodBeanDefinition extends DefaultBeanDefinition {
	private Method method;

	public MethodBeanDefinition(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> returnClass,
			Class<?> methodTargetClass, Method method) {
		super(beanFactory, propertyFactory, method.getReturnType(),
				new MethodBeanBuilder(beanFactory, propertyFactory,
						methodTargetClass, method));
		this.method = method;
	}

	public AnnotatedElement getAnnotatedElement() {
		return method;
	}
}
