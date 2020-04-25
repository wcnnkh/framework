package scw.beans.loader;

import scw.beans.BeanFactory;
import scw.util.attribute.SimpleAttributes;
import scw.util.value.property.PropertyFactory;

public class LoaderContext extends SimpleAttributes<Object, Object> {
	private static final long serialVersionUID = 1L;
	private final Class<?> targetClass;
	private final BeanFactory beanFactory;
	private final PropertyFactory propertyFactory;

	public LoaderContext(Class<?> targetClass, BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		this.targetClass = targetClass;
		this.beanFactory = beanFactory;
		this.propertyFactory = propertyFactory;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}

	public BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public PropertyFactory getPropertyFactory() {
		return propertyFactory;
	}
}
