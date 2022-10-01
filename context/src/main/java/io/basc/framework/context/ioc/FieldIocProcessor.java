package io.basc.framework.context.ioc;

import io.basc.framework.context.Context;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeansException;
import io.basc.framework.factory.FactoryException;
import io.basc.framework.mapper.Field;

public abstract class FieldIocProcessor extends IocProcessor {
	private final Field field;

	public FieldIocProcessor(Context context, Field field) {
		super(context);
		this.field = field;
	}

	public Field getField() {
		return field;
	}

	@Override
	public void processPostBean(Object bean, BeanDefinition definition) throws FactoryException {
		if (field == null) {
			return;
		}

		if (!acceptModifiers(definition, bean, field.getSetter().getModifiers())) {
			return;
		}

		checkField(bean, getField());
		processField(bean, definition, field);
	}

	public abstract void processField(Object bean, BeanDefinition definition, Field field) throws BeansException;
}
