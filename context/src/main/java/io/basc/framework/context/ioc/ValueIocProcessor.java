package io.basc.framework.context.ioc;

import io.basc.framework.context.Context;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeansException;
import io.basc.framework.mapper.Field;

public class ValueIocProcessor extends FieldIocProcessor {
	private final IocResolver iocResolver;

	public ValueIocProcessor(Context context, IocResolver iocResolver, Field field) {
		super(context, field);
		this.iocResolver = iocResolver;
	}

	@Override
	public void processField(Object bean, BeanDefinition definition, Field field) throws BeansException {
		if (!field.isSupportSetter()) {
			return;
		}

		ValueDefinition valueDefinition = iocResolver.resolveValueDefinition(field.getSetter());
		if (valueDefinition == null) {
			return;
		}

		ValueProcessor valueProcessor = getContext().getInstance(ValueProcessor.class);
		valueProcessor.process(getContext(), bean, definition, field, valueDefinition);
	}
}
