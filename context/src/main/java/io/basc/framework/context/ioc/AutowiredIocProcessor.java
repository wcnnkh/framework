package io.basc.framework.context.ioc;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.mapper.Field;

public class AutowiredIocProcessor extends FieldIocProcessor {
	private final IocResolver iocResolver;

	public AutowiredIocProcessor(Context context, IocResolver iocResolver, Field field) {
		super(context, field);
		this.iocResolver = iocResolver;
	}

	@Override
	public void processField(Object bean, BeanDefinition definition, Field field) throws BeansException {
		AutowiredDefinition autowiredDefinition = iocResolver.resolveAutowiredDefinition(field.getSetter());
		if (autowiredDefinition == null) {
			return;
		}

		Object value = null;
		for (String name : autowiredDefinition.getNames()) {
			if (getContext().isInstance(name)) {
				value = getContext().getInstance(name);
				break;
			}
		}

		if (autowiredDefinition.isRequired() && value == null) {
			throw new UnsupportedException(getField().getSetter().toString());
		}

		if (value != null) {
			if (exists(bean, field)) {
				logger.debug("field already default value, field [{}]", getField().toString());
			} else {
				getField().getSetter().set(bean, value);
			}
		}
	}

	private static boolean exists(Object instance, Field field) {
		java.lang.reflect.Field refField = null;
		if (field.isSupportGetter() && field.getGetter().getField() != null) {
			refField = field.getGetter().getField();
		} else if (field.isSupportSetter() && field.getSetter().getField() != null) {
			refField = field.getSetter().getField();
		}

		if (refField == null) {
			return false;
		}

		return ReflectionUtils.get(refField, instance) != null;
	}
}
