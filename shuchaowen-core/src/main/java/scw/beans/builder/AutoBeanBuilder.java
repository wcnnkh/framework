package scw.beans.builder;

import scw.beans.BeanFactory;
import scw.core.instance.AutoConstructorBuilder;
import scw.core.instance.ConstructorBuilder;
import scw.value.property.PropertyFactory;

public class AutoBeanBuilder extends ConstructorBeanBuilder {
	private final AutoConstructorBuilder constructorBuilder;

	public AutoBeanBuilder(LoaderContext context) {
		this(context.getBeanFactory(), context.getPropertyFactory(), context
				.getTargetClass());
	}

	public AutoBeanBuilder(BeanFactory beanFactory,
			PropertyFactory propertyFactory, Class<?> targetClass) {
		super(beanFactory, propertyFactory, targetClass);
		this.constructorBuilder = new AutoConstructorBuilder(beanFactory,
				propertyFactory, getTargetClass());
	}

	@Override
	protected ConstructorBuilder getConstructorBuilder() {
		return constructorBuilder;
	}
}
