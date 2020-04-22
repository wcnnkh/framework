package scw.beans;

import scw.core.instance.ConstructorBuilder;
import scw.lang.NotSupportedException;
import scw.util.value.property.PropertyFactory;

public abstract class ConstructorBeanBuilder extends
		AbstractBeanBuilder {

	public ConstructorBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
	}

	protected abstract ConstructorBuilder getConstructorBuilder();

	public boolean isInstance() {
		return getConstructorBuilder().getConstructor() != null;
	}

	public Object create() throws Exception {
		if (!isInstance()) {
			throw new NotSupportedException(getTargetClass().getName());
		}
		
		return createInternal(getTargetClass(), getConstructorBuilder().getConstructor(), getConstructorBuilder().getArgs());
	}
}
