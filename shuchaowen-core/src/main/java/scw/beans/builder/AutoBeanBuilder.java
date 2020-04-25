package scw.beans.builder;

import scw.beans.BeanFactory;
import scw.beans.loader.LoaderContext;
import scw.core.instance.AutoConstructorBuilder;
import scw.core.instance.ConstructorBuilder;
import scw.core.parameter.ParameterUtils;
import scw.util.value.property.PropertyFactory;

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
				propertyFactory, getTargetClass(),
				ParameterUtils.getParameterDescriptorFactory());
	}

	@Override
	protected ConstructorBuilder getConstructorBuilder() {
		return constructorBuilder;
	}
}
