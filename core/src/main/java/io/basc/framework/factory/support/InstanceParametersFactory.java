package io.basc.framework.factory.support;

import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterDescriptors;
import io.basc.framework.factory.AbstractParametersFactory;
import io.basc.framework.factory.DefaultParameterFactory;
import io.basc.framework.factory.InstanceFactory;

public abstract class InstanceParametersFactory extends AbstractParametersFactory {
	private final InstanceFactory instanceFactory;
	private final DefaultParameterFactory defaultParameterFactory;

	public InstanceParametersFactory(InstanceFactory instanceFactory, DefaultParameterFactory defaultParameterFactory) {
		this.instanceFactory = instanceFactory;
		this.defaultParameterFactory = defaultParameterFactory;
	}

	public InstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public DefaultParameterFactory getDefaultParameterFactory() {
		return defaultParameterFactory;
	}

	protected String getParameterName(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor) {
		return parameterDescriptors.getDeclaringClass().getName() + "." + parameterDescriptor.getName();
	}

	protected String getInstanceName(ParameterDescriptors parameterDescriptors,
			ParameterDescriptor parameterDescriptor) {
		if (getInstanceFactory().isInstance(parameterDescriptor.getType())) {
			return parameterDescriptor.getType().getName();
		}

		String name = getParameterName(parameterDescriptors, parameterDescriptor);
		if (getInstanceFactory().isInstance(name)) {
			return name;
		}

		return null;
	}

	@Override
	protected boolean isAccept(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		if (parameterDescriptor.isNullable()) {
			return true;
		}

		if (parameterDescriptor.getType() == parameterDescriptors.getDeclaringClass()) {
			return false;
		}

		String name = getInstanceName(parameterDescriptors, parameterDescriptor);
		if (name == null) {
			return false;
		}
		return true;
	}

	@Override
	protected Object getParameter(ParameterDescriptors parameterDescriptors, ParameterDescriptor parameterDescriptor,
			int index) {
		String name = getInstanceName(parameterDescriptors, parameterDescriptor);
		return name == null ? null : getInstanceFactory().getInstance(name);
	}
}
