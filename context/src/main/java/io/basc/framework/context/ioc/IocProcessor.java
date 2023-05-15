package io.basc.framework.context.ioc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.factory.BeanPostProcessor;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;

public abstract class IocProcessor implements BeanPostProcessor {
	protected static Logger logger = LoggerFactory.getLogger(IocProcessor.class);
	private final Context context;

	public IocProcessor(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

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
			logger.warn("class [{}] field [{}] is a static", field.getSetter().getDeclaringClass(),
					field.getSetter().getName());
		}
	}
}
