package io.basc.framework.context.config.support;

import java.lang.reflect.Method;

import io.basc.framework.beans.factory.Scope;
import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.beans.factory.config.support.ClassBeanDefinition;
import io.basc.framework.beans.factory.config.support.DefaultBeanDefinition;
import io.basc.framework.beans.factory.config.support.MethodBeanDefinition;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public abstract class BeanDefinitionRegistryContextPostProcessor implements ContextPostProcessor {

	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		for (Class<?> clazz : context.getContextClasses().getServices()) {
			if (!canResolveBeanDefinition(clazz)) {
				continue;
			}

			BeanDefinition beanDefinition = resolveBeanDefinition(clazz);
			if (!registerBeanDefinition(context, beanDefinition)) {
				continue;
			}

			for (Method method : getResolveMethod(clazz)) {
				if (!canResolveBeanDefinition(clazz, beanDefinition, method)) {
					continue;
				}

				BeanDefinition methodBeanDefinition = resolveBeanDefinition(clazz, beanDefinition, method);
				registerBeanDefinition(context, methodBeanDefinition);
			}
		}
	}

	protected abstract boolean canResolveBeanDefinition(Class<?> clazz);

	protected Elements<Method> getResolveMethod(Class<?> clazz) {
		return ReflectionUtils.getDeclaredMethods(clazz).getElements();
	}

	protected abstract boolean canResolveBeanDefinition(Class<?> clazz, BeanDefinition originBeanDefinition,
			Method method);

	protected Scope getScop(Class<?> clazz) {
		return Scope.DEFAULT;
	}

	protected Scope getScop(Class<?> clazz, Method method) {
		return Scope.DEFAULT;
	}

	protected boolean isSingleton(Class<?> clazz) {
		return true;
	}

	protected boolean isSingleton(Class<?> clazz, Method method) {
		return true;
	}
	
	@Nullable
	protected BeanDefinition resolveBeanDefinition(Class<?> clazz) {
		return new ClassBeanDefinition(getBeanName(clazz), clazz, getScop(clazz), isSingleton(clazz));
	}

	protected boolean registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry,
			BeanDefinition beanDefinition) {
		beanDefinitionRegistry.registerBeanDefinition(beanDefinition);
		return true;
	}

	protected BeanDefinition resolveBeanDefinition(Class<?> clazz, BeanDefinition originBeanDefinition, Method method) {
		return new MethodBeanDefinition(getBeanName(clazz, method), getScop(clazz, method), isSingleton(clazz, method),
				originBeanDefinition, method, clazz);
	}

	protected String getBeanName(Class<?> clazz) {
		return clazz.getName();
	}

	protected String getBeanName(Class<?> clazz, Method method) {
		return method.getName();
	}
}
