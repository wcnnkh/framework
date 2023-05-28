package io.basc.framework.factory.support;

import java.util.Collection;
import java.util.Map;

import io.basc.framework.beans.BeanResolver;
import io.basc.framework.beans.BeanResolverExtend;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.factory.ParameterFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterDescriptors;
import io.basc.framework.value.Value;

public class InstanceParameterFactory extends AbstractParametersFactory
		implements ParameterFactory, BeanResolverExtend {
	private final InstanceFactory instanceFactory;

	public InstanceParameterFactory(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	public InstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	protected String getInstanceName(ParameterDescriptor parameterDescriptor) {
		if (getInstanceFactory().isInstance(parameterDescriptor.getType())) {
			return parameterDescriptor.getType().getName();
		}

		if (getInstanceFactory().isInstance(parameterDescriptor.getName())) {
			return parameterDescriptor.getName();
		}
		return null;
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		if (parameterDescriptor.getType() == parameterDescriptors.getDeclaringClass()) {
			return false;
		}
		return isAccept(parameterDescriptor);
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		return getParameter(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		if (Value.isBaseType(parameterDescriptor.getType())
				|| Collection.class.isAssignableFrom(parameterDescriptor.getType())
				|| Map.class.isAssignableFrom(parameterDescriptor.getType())) {
			return false;
		}

		if (parameterDescriptor.isNullable()) {
			return true;
		}

		String name = getInstanceName(parameterDescriptor);
		if (name == null) {
			return false;
		}
		return true;
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {

		String name = getInstanceName(parameterDescriptor);
		return name == null ? null : getInstanceFactory().getInstance(name);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		return isAccept(parameterDescriptor);
	}

	@Override
	public boolean isAccept(ParameterDescriptors parameterDescriptors, BeanResolver chain) {
		return isAccept(parameterDescriptors);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		if (isAccept(parameterDescriptor)) {
			return getParameter(parameterDescriptor);
		}
		return BeanResolverExtend.super.getParameter(parameterDescriptor, chain);
	}

	@Override
	public Object[] getParameters(ParameterDescriptors parameterDescriptors, BeanResolver chain) {
		if (isAccept(parameterDescriptors)) {
			return getParameters(parameterDescriptors);
		}
		return BeanResolverExtend.super.getParameters(parameterDescriptors, chain);
	}

	@Override
	public boolean isNullable(ParameterDescriptor parameterDescriptor, BeanResolver chain) {
		Nullable nullable = parameterDescriptor.getAnnotation(Nullable.class);
		if (nullable != null) {
			return nullable.value();
		}
		return BeanResolverExtend.super.isNullable(parameterDescriptor, chain);
	}
}
