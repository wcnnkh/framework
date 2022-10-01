package io.basc.framework.context.ioc.annotation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.AutowiredDefinition;
import io.basc.framework.context.ioc.AutowiredIocProcessor;
import io.basc.framework.context.ioc.BeanMethodProcessor;
import io.basc.framework.context.ioc.IocResolver;
import io.basc.framework.context.ioc.IocResolverExtend;
import io.basc.framework.context.ioc.MethodIocDefinition;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.context.ioc.ValueIocProcessor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.factory.BeanPostProcessor;
import io.basc.framework.factory.BeanResolver;
import io.basc.framework.factory.BeanResolverExtend;
import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFeature;
import io.basc.framework.mapper.Fields;

public class IocBeanResolverExtend implements BeanResolverExtend, IocResolverExtend {
	private final Context context;
	private final IocResolver iocResolver;

	public IocBeanResolverExtend(Context context, IocResolver iocResolver) {
		this.context = context;
		this.iocResolver = iocResolver;
	}

	@Override
	public Collection<BeanPostProcessor> resolveDependenceProcessors(TypeDescriptor typeDescriptor,
			BeanResolver chain) {
		List<BeanPostProcessor> postProcessors = new ArrayList<BeanPostProcessor>();
		postProcessors.addAll(BeanResolverExtend.super.resolveDependenceProcessors(typeDescriptor, chain));
		for (Field field : Fields.getFields(typeDescriptor.getType()).filter(FieldFeature.SUPPORT_SETTER).all()) {
			if (iocResolver.resolveAutowiredDefinition(field.getSetter()) != null) {
				postProcessors.add(new AutowiredIocProcessor(context, iocResolver, field));
			}

			if (iocResolver.resolveValueDefinition(field.getSetter()) != null) {
				postProcessors.add(new ValueIocProcessor(context, iocResolver, field));
			}
		}
		return postProcessors;
	}

	@Override
	public Collection<BeanPostProcessor> resolveDestroyProcessors(TypeDescriptor typeDescriptor, BeanResolver chain) {
		List<BeanPostProcessor> postProcessors = new ArrayList<BeanPostProcessor>();
		postProcessors.addAll(BeanResolverExtend.super.resolveDestroyProcessors(typeDescriptor, chain));
		ReflectionUtils.getDeclaredMethods(typeDescriptor.getType()).stream().forEach((method) -> {
			if (iocResolver.resolveDestroyDefinition(method) != null) {
				BeanMethodProcessor iocProcessor = new BeanMethodProcessor(context, method);
				postProcessors.add(iocProcessor);
			}
		});
		return postProcessors;
	}

	@Override
	public Collection<BeanPostProcessor> resolveInitProcessors(TypeDescriptor typeDescriptor, BeanResolver chain) {
		List<BeanPostProcessor> postProcessors = new ArrayList<BeanPostProcessor>();
		postProcessors.addAll(BeanResolverExtend.super.resolveInitProcessors(typeDescriptor, chain));
		ReflectionUtils.getDeclaredMethods(typeDescriptor.getType()).stream().forEach((method) -> {
			ReflectionUtils.makeAccessible(method);
			if (iocResolver.resolveInitDefinition(method) != null) {
				BeanMethodProcessor iocProcessor = new BeanMethodProcessor(context, method);
				postProcessors.add(iocProcessor);
			}
		});
		return postProcessors;
	}

	@Override
	public AutowiredDefinition resolveAutowiredDefinition(ParameterDescriptor parameterDescriptor, IocResolver chain) {
		Autowired autowired = parameterDescriptor.getAnnotation(Autowired.class);
		if (autowired != null) {
			AutowiredDefinition autowiredDefinition = new AutowiredDefinition();
			autowiredDefinition.setNames(Arrays.asList(autowired.value(), parameterDescriptor.getType().getName()));
			autowiredDefinition.setRequired(autowired.required());
			return autowiredDefinition;
		}
		return IocResolverExtend.super.resolveAutowiredDefinition(parameterDescriptor, chain);
	}

	@Override
	public ValueDefinition resolveValueDefinition(ParameterDescriptor parameterDescriptor, IocResolver chain) {
		Value value = parameterDescriptor.getAnnotation(Value.class);
		if (value != null) {
			ValueDefinition definition = new ValueDefinition();
			definition.setNames(Arrays.asList(value.value()));
			definition.setListener(value.listener());
			definition.setCharsetName(value.charsetName());
			definition.setValueProcessor(value.processor());
			return definition;
		}
		return IocResolverExtend.super.resolveValueDefinition(parameterDescriptor, chain);
	}

	@Override
	public MethodIocDefinition resolveDestroyDefinition(Method method, IocResolver chain) {
		Destroy destroy = method.getAnnotation(Destroy.class);
		if (destroy != null) {
			MethodIocDefinition definition = new MethodIocDefinition();
			return definition;
		}
		return IocResolverExtend.super.resolveDestroyDefinition(method, chain);
	}

	@Override
	public MethodIocDefinition resolveInitDefinition(Method method, IocResolver chain) {
		InitMethod initMethod = method.getAnnotation(InitMethod.class);
		if (initMethod != null) {
			MethodIocDefinition definition = new MethodIocDefinition();
			return definition;
		}
		return IocResolverExtend.super.resolveInitDefinition(method, chain);
	}
}
