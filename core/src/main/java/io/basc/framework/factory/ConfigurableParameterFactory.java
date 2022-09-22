package io.basc.framework.factory;

import io.basc.framework.core.parameter.ParameterDescriptor;

public class ConfigurableParameterFactory extends ConfigurableServices<ParameterFactory> implements ParameterFactory {

	public ConfigurableParameterFactory() {
		super(ParameterFactory.class);
	}

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		for (ParameterFactory factory : this) {
			if (factory.isAccept(parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		for (ParameterFactory factory : this) {
			if (factory.isAccept(parameterDescriptor)) {
				return factory.getParameter(parameterDescriptor);
			}
		}
		return null;
	}

}
