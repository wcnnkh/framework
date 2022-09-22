package io.basc.framework.context.annotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.ProviderDefinition;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.lang.Ignore;
import io.basc.framework.util.ArrayUtils;
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
		providerDefinition.setNames(Arrays.asList(provider.value()));
		providerDefinition.setOrder(provider.order());
		return providerDefinition;
	}

	@Override
	public String getId(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Bean bean = AnnotationUtils.getAnnotation(Bean.class, typeDescriptor, typeDescriptor.getType());
		if (bean != null && StringUtils.isNotEmpty(bean.value())) {
			return bean.value();
		}
		return BeanResolverExtend.super.getId(typeDescriptor, chain);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Bean bean = AnnotationUtils.getAnnotation(Bean.class, typeDescriptor, typeDescriptor.getType());
		if (bean != null && bean.names().length > 0) {
			return Arrays.asList(bean.names());
		}
		return BeanResolverExtend.super.getNames(typeDescriptor, chain);
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
		java.util.List<BeanDefinition> definitions = null;
		Service service = clazz.getAnnotation(Service.class);
		if (service != null) {
			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}
			ContextBeanDefinition definition = new ContextBeanDefinition(context, clazz);
			Set<String> names = new LinkedHashSet<String>();
			names.addAll(definition.getNames());
			names.addAll(getInternalNames(clazz, service));
			definition.setNames(names);
			definitions.add(definition);
		}

		for (Method method : clazz.getDeclaredMethods()) {
			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}

			Bean bean = method.getAnnotation(Bean.class);
			if (bean == null) {
				continue;
			}

			BeanDefinition beanDefinition = new ExecutableBeanDefinition(context, clazz, method);
			definitions.add(beanDefinition);
		}

		for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
			if (definitions == null) {
				definitions = new ArrayList<BeanDefinition>(8);
			}

			Bean bean = constructor.getAnnotation(Bean.class);
			if (bean == null) {
				continue;
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

	private static Collection<String> getInternalNames(Class<?> clazz, Service service) {
		if (!ArrayUtils.isEmpty(service.value())) {
			return Collections.emptyList();
		}

		Class<?> serviceInterface = getServiceInterface(clazz);
		if (serviceInterface == null) {
			return Collections.emptyList();
		}

		HashSet<String> list = new HashSet<String>();
		list.add(serviceInterface.getName());
		return list;
	}

	private static Class<?> getServiceInterface(Class<?> clazz) {
		return ClassUtils.getInterfaces(clazz).streamAll().filter((i) -> {
			if (i.isAnnotationPresent(Ignore.class) || i.getMethods().length == 0) {
				return false;
			}
			return true;
		}).findFirst().orElse(null);
	}
}
