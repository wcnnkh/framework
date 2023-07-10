package io.basc.framework.context.config.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import io.basc.framework.beans.factory.config.BeanDefinition;
import io.basc.framework.beans.factory.config.BeanDefinitionRegistry;
import io.basc.framework.beans.factory.config.support.BeanFactoryExecutor;
import io.basc.framework.beans.factory.config.support.DefaultBeanDefinition;
import io.basc.framework.context.config.Condition;
import io.basc.framework.context.config.ConfigurableContext;
import io.basc.framework.context.config.ContextPostProcessor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.execution.reflect.ConstructorExecutor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Elements;

public abstract class BeanDefinitionRegistryContextPostProcessor implements ContextPostProcessor {
	@Override
	public void postProcessContext(ConfigurableContext context) throws Throwable {
		for (Class<?> clazz : context.getContextClasses().getServices()) {
			if (!canResolveBeanDefinition(clazz)) {
				continue;
			}

			String beanName = getBeanName(clazz);
			BeanDefinition beanDefinition = resolveBeanDefinition(context, clazz);
			if (registerBeanDefinition(context, beanName, beanDefinition)) {
				for (String alias : getAliasNames(clazz)) {
					context.registerAlias(beanName, alias);
				}

				for (Method method : getResolveMethod(clazz)) {
					if (!canResolveBeanDefinition(clazz, beanDefinition, method)) {
						continue;
					}

					String methodBeanName = getBeanName(clazz, method);
					BeanDefinition methodBeanDefinition = resolveBeanDefinition(context, clazz, beanName,
							beanDefinition, method);
					if (registerBeanDefinition(context, methodBeanName, methodBeanDefinition)) {
						for (String alias : getAliasNames(clazz, method)) {
							context.registerAlias(methodBeanName, alias);
						}
					}
				}
			}
		}
	}

	protected Elements<String> getAliasNames(Class<?> clazz) {
		return Elements.empty();
	}

	protected Elements<String> getAliasNames(Class<?> clazz, Method method) {
		return Elements.empty();
	}

	@Nullable
	protected Condition getCondition(AnnotatedElement annotatedElement) {
		return null;
	}

	protected abstract boolean canResolveBeanDefinition(Class<?> clazz);

	protected Elements<Method> getResolveMethod(Class<?> clazz) {
		return ReflectionUtils.getDeclaredMethods(clazz).getElements();
	}

	protected abstract boolean canResolveBeanDefinition(Class<?> clazz, BeanDefinition originBeanDefinition,
			Method method);

	@Nullable
	protected DefaultBeanDefinition<ConstructorExecutor> resolveBeanDefinition(ConfigurableContext context,
			Class<?> clazz) {
		DefaultBeanDefinition<ConstructorExecutor> beanDefinition = new DefaultBeanDefinition<>();
		return beanDefinition;
	}

	protected boolean registerBeanDefinition(BeanDefinitionRegistry beanDefinitionRegistry, String beanName,
			BeanDefinition beanDefinition) {
		beanDefinitionRegistry.registerBeanDefinition(beanName, beanDefinition);
		return true;
	}

	protected DefaultBeanDefinition<BeanFactoryExecutor> resolveBeanDefinition(ConfigurableContext context,
			Class<?> clazz, String originBeanName, BeanDefinition originBeanDefinition, Method method) {
		DefaultBeanDefinition<BeanFactoryExecutor> beanDefinition = new DefaultBeanDefinition<>();
		BeanFactoryExecutor beanFactoryExecutor = new BeanFactoryExecutor(TypeDescriptor.valueOf(clazz), method,
				originBeanName);
		beanDefinition.setExecutors(Elements.singleton(beanFactoryExecutor));
		return beanDefinition;
	}

	protected String getBeanName(Class<?> clazz) {
		return clazz.getName();
	}

	protected String getBeanName(Class<?> clazz, Method method) {
		return method.getName();
	}
}
