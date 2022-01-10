package io.basc.framework.beans.ioc;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.MapperUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class AbstractIocProcessor implements IocProcessor {
	protected static Logger logger = LoggerFactory.getLogger(IocProcessor.class);

	protected boolean acceptModifiers(BeanDefinition beanDefinition, Object bean, int modifiers) {
		if (bean == null) {
			return Modifier.isStatic(modifiers);
		}
		return true;
	}

	protected void checkMethod(Method method) {
		if (Modifier.isStatic(method.getModifiers())) {
			logger.warn("class [{}] method [{}] is a static", method.getDeclaringClass(), method);
		}
	}

	public void checkField(Object obj, Field field) {
		if (Modifier.isStatic(field.getSetter().getModifiers())) {
			logger.warn("class [{}] field [{}] is a static", field.getSetter().getSourceClass(),
					field.getSetter().getName());
		}

		if (MapperUtils.isExistValue(field, obj)) {
			logger.warn("class[{}] fieldName[{}] existence default value", field.getSetter().getSourceClass(),
					field.getSetter().getName());
		}
	}
}
