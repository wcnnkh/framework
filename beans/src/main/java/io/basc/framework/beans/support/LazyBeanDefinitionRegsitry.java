package io.basc.framework.beans.support;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.aop.support.UnmodifiableMethodInterceptors;
import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanDefinitionLoader;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.annotation.Proxy;
import io.basc.framework.context.annotation.ProviderClassesLoader;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.ServiceLoader;
import io.basc.framework.factory.support.InstanceIterable;
import io.basc.framework.lang.NestedExceptionUtils;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.ClassUtils;

public class LazyBeanDefinitionRegsitry extends DefaultBeanDefinitionRegistry {
	private static Logger logger = LoggerFactory.getLogger(LazyBeanDefinitionRegsitry.class);
	private ConfigurableBeanFactory beanFactory;

	public LazyBeanDefinitionRegsitry(ConfigurableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	private BeanDefinition load(Class<?> clazz) {
		if (BeanDefinitionLoader.class.isAssignableFrom(clazz)) {
			return null;
		}

		ServiceLoader<BeanDefinitionLoader> serviceLoader = beanFactory.getServiceLoader(BeanDefinitionLoader.class);
		return new IteratorBeanDefinitionLoaderChain(serviceLoader.iterator()).load(beanFactory, clazz);
	}

	private BeanDefinition reference(Class<?> sourceClass) {
		// 未注解service时接口默认实现
		if (sourceClass.isInterface()) {
			String name = sourceClass.getName() + "Impl";
			Class<?> targetClass = ClassUtils.getClass(name, beanFactory.getClassLoader());
			if (targetClass != null && sourceClass.isAssignableFrom(targetClass)
					&& beanFactory.isInstance(targetClass)) {
				return beanFactory.getDefinition(targetClass.getName());
			}

			int index = sourceClass.getName().lastIndexOf(".");
			name = index == -1 ? (sourceClass.getName() + "Impl")
					: (sourceClass.getName().substring(0, index) + ".impl." + sourceClass.getSimpleName() + "Impl");
			targetClass = ClassUtils.getClass(name, beanFactory.getClassLoader());
			if (targetClass != null && sourceClass.isAssignableFrom(targetClass)
					&& beanFactory.isInstance(targetClass)) {
				return beanFactory.getDefinition(targetClass.getName());
			}
		}
		return null;
	}

	private BeanDefinition provider(Class<?> sourceClass) {
		ProviderClassesLoader classesLoader = new ProviderClassesLoader(beanFactory.getContextClasses(), sourceClass);
		for (Class<?> impl : classesLoader) {
			BeanDefinition definition = super.getDefinition(impl);
			if (definition == null) {
				definition = new DefaultBeanDefinition(beanFactory, impl);
			}

			if (definition.isInstance()) {
				logger.info("Service provider {} impl {}", sourceClass, impl);
				return definition;
			}
		}
		return null;
	}

	private BeanDefinition proxy(Class<?> sourceClass) {
		if (sourceClass.isInterface() || Modifier.isAbstract(sourceClass.getModifiers())) {
			Proxy proxy = sourceClass.getAnnotation(Proxy.class);
			if (proxy != null) {
				DefaultBeanDefinition definition = new DefaultBeanDefinition(beanFactory, sourceClass);
				MethodInterceptor methodInterceptor = new UnmodifiableMethodInterceptors(
						new InstanceIterable<MethodInterceptor>(beanFactory, getProxyNames(proxy)));
				definition.getMethodInterceptors().addFirstMethodInterceptor(methodInterceptor);
				return definition;
			}
		}
		return null;
	}

	public BeanDefinition load(String name) {
		Class<?> clazz = ClassUtils.getClass(name, beanFactory.getClassLoader());
		if (clazz == null) {
			return null;
		}

		if (clazz.isPrimitive() || clazz.isEnum() || clazz.isArray() || !ClassUtils.isAvailable(clazz)) {
			return null;
		}

		if (!ReflectionUtils.isAvailable(clazz, (e) -> {
			if (logger.isTraceEnabled()) {
				logger.trace(e, "This class[{}] cannot be included because:", clazz.getName());
			} else if (logger.isDebugEnabled()) {
				logger.debug("This class[{}] cannot be included because: {}", clazz.getName(),
						NestedExceptionUtils.getNonEmptyMessage(e, false));
			}
			return false;
		})) {
			return null;
		}

		BeanDefinition definition = null;
		if (definition == null) {
			definition = provider(clazz);
		}

		if (definition == null) {
			definition = load(clazz);
		}

		if (definition == null) {
			definition = reference(clazz);
		}

		if (definition == null) {
			definition = proxy(clazz);
		}
		return definition == null ? new DefaultBeanDefinition(beanFactory, clazz) : definition;
	}

	@Override
	public BeanDefinition getDefinition(String name) {
		BeanDefinition definition = super.getDefinition(name);
		if (definition == null) {
			synchronized (getDefinitionMutex()) {
				definition = super.getDefinition(name);
				if (definition == null) {
					BeanDefinition first = load(name);
					definition = first;
					if (definition == null || !definition.isInstance()) {
						String[] aliases = getAliases(name);
						for (String alias : aliases) {
							definition = load(alias);
							if (definition != null && definition.isInstance()) {
								break;
							}
						}
					}

					if (definition == null) {
						definition = first;
					}

					if (definition != null) {
						if (logger.isTraceEnabled()) {
							logger.trace("lazy load beanName name [{}] definition {}", name, definition);
						}

						definition = registerDefinition(name, definition);
					}
				}
			}
		}
		return definition;
	}

	private static List<String> getProxyNames(Proxy proxy) {
		if (proxy == null) {
			return Collections.emptyList();
		}

		List<String> list = new ArrayList<String>();
		for (String name : proxy.names()) {
			list.add(name);
		}

		for (Class<? extends MethodInterceptor> c : proxy.value()) {
			list.add(c.getName());
		}

		return Arrays.asList(list.toArray(new String[0]));
	}
}
