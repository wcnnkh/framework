package io.basc.framework.context.annotation;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanPostProcessor;
import io.basc.framework.context.Context;
import io.basc.framework.context.ContextResolver;
import io.basc.framework.context.ContextResolverExtend;
import io.basc.framework.context.ProviderDefinition;
import io.basc.framework.context.ioc.BeanMethodProcessor;
import io.basc.framework.context.support.ContextBeanDefinition;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.filter.AnnotationTypeFilter;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.lang.Ignore;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.StringUtils;

public class AnnotationContextResolverExtend extends AnnotationTypeFilter
		implements ContextResolverExtend, Ordered, BeanResolverExtend {
	private final Context context;

	public AnnotationContextResolverExtend(Context context) {
		super(Indexed.class);
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
		Component component = AnnotatedElementUtils.getMergedAnnotation(typeDescriptor, Component.class);
		if (component != null && StringUtils.isNotEmpty(component.value())) {
			return component.value();
		}
		return BeanResolverExtend.super.getId(typeDescriptor, chain);
	}

	@Override
	public Collection<String> getNames(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Set<String> names = null;
		Bean bean = AnnotatedElementUtils.getMergedAnnotation(typeDescriptor, Bean.class);
		if (bean != null && bean.name().length > 0) {
			if (names == null) {
				names = new HashSet<>(8);
			}
			names.addAll(Arrays.asList(bean.name()));
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

		Collection<String> parentNames = BeanResolverExtend.super.getNames(typeDescriptor, chain);
		if (!CollectionUtils.isEmpty(parentNames)) {
			if (names == null) {
				names = new HashSet<>(8);
			}
			names.addAll(parentNames);
		}
		return names == null ? Collections.emptyList() : names;
	}

	@Override
	public boolean isAopEnable(TypeDescriptor typeDescriptor, BeanResolver chain) {
		AopEnable aopEnable = AnnotatedElementUtils.getMergedAnnotation(typeDescriptor, AopEnable.class);
		if (aopEnable != null) {
			return aopEnable.value();
		}
		return BeanResolverExtend.super.isAopEnable(typeDescriptor, chain);
	}

	@Override
	public boolean canResolveExecutable(Class<?> sourceClass, ContextResolver chain) {
		if (AnnotatedElementUtils.hasAnnotation(sourceClass, Configuration.class)) {
			return true;
		}
		return ContextResolverExtend.super.canResolveExecutable(sourceClass, chain);
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass, ContextResolver chain) {
		Component component = AnnotatedElementUtils.getMergedAnnotation(sourceClass, Component.class);
		if (component == null) {
			return chain.resolveBeanDefinition(sourceClass);
		}
		return new ContextBeanDefinition(context, sourceClass);
	}

	@Override
	public BeanDefinition resolveBeanDefinition(Class<?> sourceClass, Executable executable, ContextResolver chain) {
		Bean bean = executable.getAnnotation(Bean.class);
		if (bean == null) {
			return chain.resolveBeanDefinition(sourceClass, executable);
		}
		return new ExecutableBeanDefinition(context, sourceClass, executable);
	}

	private static Class<?> getServiceInterface(Class<?> clazz) {
		return ClassUtils.getInterfaces(clazz).all().getElements().filter((i) -> {
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

	private BeanPostProcessor getBeanPostProcessorByMethod(TypeDescriptor typeDescriptor, String name) {
		Method method = ReflectionUtils.getDeclaredMethods(typeDescriptor.getType()).all().find(name);
		if (method == null) {
			return null;
		}
		return new BeanMethodProcessor(context, method);
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Bean bean = typeDescriptor.getAnnotation(Bean.class);
		if (bean == null || bean.destroyMethod().length == 0) {
			return BeanResolverExtend.super.resolveDestroyProcessors(typeDescriptor, chain);
		}

		Collection<BeanPostProcessor> beanPostProcessors = null;
		for (String name : bean.destroyMethod()) {
			BeanPostProcessor beanPostProcessor = getBeanPostProcessorByMethod(typeDescriptor, name);
			if (beanPostProcessor != null) {
				if (beanPostProcessors == null) {
					beanPostProcessors = new ArrayList<>(8);
				}
				beanPostProcessors.add(beanPostProcessor);
			}
		}

		Collection<BeanPostProcessor> sources = BeanResolverExtend.super.resolveDestroyProcessors(typeDescriptor,
				chain);
		if (!CollectionUtils.isEmpty(sources)) {
			if (beanPostProcessors == null) {
				beanPostProcessors = new ArrayList<>(sources.size());
			}

			beanPostProcessors.addAll(sources);
		}
		return CollectionUtils.isEmpty(beanPostProcessors) ? Collections.emptyList() : beanPostProcessors;
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor, BeanResolver chain) {
		Bean bean = typeDescriptor.getAnnotation(Bean.class);
		if (bean == null || bean.initMethod().length == 0) {
			return BeanResolverExtend.super.resolveInitProcessors(typeDescriptor, chain);
		}

		Collection<BeanPostProcessor> beanPostProcessors = null;
		for (String name : bean.initMethod()) {
			BeanPostProcessor beanPostProcessor = getBeanPostProcessorByMethod(typeDescriptor, name);
			if (beanPostProcessor != null) {
				if (beanPostProcessors == null) {
					beanPostProcessors = new ArrayList<>(8);
				}
				beanPostProcessors.add(beanPostProcessor);
			}
		}

		Collection<BeanPostProcessor> sources = BeanResolverExtend.super.resolveInitProcessors(typeDescriptor, chain);
		if (!CollectionUtils.isEmpty(sources)) {
			if (beanPostProcessors == null) {
				beanPostProcessors = new ArrayList<>(sources.size());
			}

			beanPostProcessors.addAll(sources);
		}
		return CollectionUtils.isEmpty(beanPostProcessors) ? Collections.emptyList() : beanPostProcessors;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory,
			ContextResolver chain) throws IOException {
		if (!EnableConditionUtils.enable(metadataReader, context.getProperties())) {
			return false;
		}

		if (super.match(metadataReader, metadataReaderFactory)) {
			return true;
		}
		return ContextResolverExtend.super.match(metadataReader, metadataReaderFactory, chain);
	}
}
