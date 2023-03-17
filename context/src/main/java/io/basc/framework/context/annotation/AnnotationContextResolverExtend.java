package io.basc.framework.context.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.ProviderDefinition;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.Annotations;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.lang.Ignore;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public class AnnotationContextResolverExtend implements ContextResolverExtend, Ordered, BeanResolverExtend {
	private final Context context;

	public AnnotationContextResolverExtend(Context context) {
		this.context = context;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public ProviderDefinition getProviderDefinition(Class<?> clazz, ContextResolver chain) {
		Provider provider = clazz.getAnnotation(Provider.class);
		if (provider == null) {
			return ContextResolverExtend.super.getProviderDefinition(clazz, chain);
		}

		ProviderDefinition providerDefinition = new ProviderDefinition();
		providerDefinition.setAssignable(provider.assignableValue());
		providerDefinition.setExcludes(Arrays.asList(provider.excludes()));
		providerDefinition
				.setNames(provider.value().length == 0 ? Arrays.asList(clazz) : Arrays.asList(provider.value()));
		providerDefinition.setOrder(provider.order());
		return providerDefinition;
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Bean bean = Annotations.getAnnotation(Bean.class, typeDescriptor);
		if (bean != null && StringUtils.isNotEmpty(bean.value())) {
			return bean.value();
		}
		return BeanResolverExtend.super.getId(typeDescriptor, chain);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Set<String> names = null;
		Bean bean = Annotations.getAnnotation(Bean.class, typeDescriptor, typeDescriptor.getType());
		if (bean != null && bean.names().length > 0) {
			if (names == null) {
				names = new HashSet<>(8);
			}
			names.addAll(Arrays.asList(bean.names()));
		}

		Collection<String> parentNames = BeanResolverExtend.super.getNames(typeDescriptor, chain);
		if (!CollectionUtils.isEmpty(parentNames)) {
			if (names == null) {
				names = new HashSet<>(8);
			}

			names.addAll(parentNames);
		}

		if (CollectionUtils.isEmpty(names)) {
			// 如果不存在bean定义，使用service方式定义别名
			Service service = typeDescriptor.getAnnotation(Service.class);
			if (service != null) {
				Class<?> serviceInterface = getServiceInterface(typeDescriptor.getType());
				if (serviceInterface != null) {
					if (names == null) {
						names = new HashSet<>(8);
					}
					names.add(serviceInterface.getName());
				}
			}
		}
		return names == null ? Collections.emptyList() : names;
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Service service = typeDescriptor.getType().getAnnotation(Service.class);
		if (service != null) {
			return true;
		}
		return BeanResolverExtend.super.isAopEnable(typeDescriptor, chain);
	}

	@Override
	public Collection<BeanDefinition> resolveBeanDefinitions(Class<?> clazz, ContextResolver chain) {
		List<BeanDefinition> definitions = null;
		if (AnnotatedElementUtils.hasAnnotation(clazz, Indexed.class)) {
			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}

			definitions.add(new ContextBeanDefinition(context, clazz));
		}

		for (Method method : clazz.getDeclaredMethods()) {
			Bean bean = method.getAnnotation(Bean.class);
			if (bean == null) {
				continue;
			}

			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}

			BeanDefinition beanDefinition = new ExecutableBeanDefinition(context, clazz, method);
			definitions.add(beanDefinition);
		}

		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			Bean bean = constructor.getAnnotation(Bean.class);
			if (bean == null) {
				continue;
			}

			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}

			BeanDefinition beanDefinition = new ExecutableBeanDefinition(context, clazz, constructor);
			definitions.add(beanDefinition);
		}

		Collection<BeanDefinition> superDefinitions = ContextResolverExtend.super.resolveBeanDefinitions(clazz, chain);
		if (!CollectionUtils.isEmpty(superDefinitions)) {
			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}

			definitions.addAll(superDefinitions);
		}
		return definitions == null ? Collections.emptyList() : definitions;
	}

	private static Class<?> getServiceInterface(Class<?> clazz) {
		return ClassUtils.getInterfaces(clazz).all().stream().filter((i) -> {
			if (i.isAnnotationPresent(Ignore.class) || i.getMethods().length == 0) {
				return false;
			}
			return true;
		}).findFirst().orElse(null);
	}

	@Override
	public boolean isSingleton(TypeDescriptor type, BeanResolver chain) {
		Singleton singleton = AnnotatedElementUtils.getMergedAnnotation(type, Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}

		Boolean b = isSingleton(type.getType());
		if (b != null) {
			return b;
		}
		return chain.isSingleton(type);
	}

	private Boolean isSingleton(Class<?> type) {
		if (type == null) {
			return null;
		}

		Singleton singleton = AnnotatedElementUtils.getMergedAnnotation(type, Singleton.class);
		if (singleton != null) {
			return singleton.value();
		}

		Boolean b = isSingleton(type.getSuperclass());
		if (b != null) {
			return b;
		}

		for (Class<?> interfaceClass : type.getInterfaces()) {
			b = isSingleton(interfaceClass);
			if (b != null) {
				return b;
			}
		}
		return null;
	}

	@Override
	public Object getDefaultParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		DefaultValue defaultValue = AnnotatedElementUtils.getMergedAnnotation(parameterDescriptor, DefaultValue.class);
		if (defaultValue != null) {
			return defaultValue.value();
		}
		return chain.getDefaultParameter(parameterDescriptor);
	}
}
