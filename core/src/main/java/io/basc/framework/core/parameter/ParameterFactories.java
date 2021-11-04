package io.basc.framework.core.parameter;

import io.basc.framework.factory.ConfigurableServices;

public class ParameterFactories extends ConfigurableServices<ParameterFactory> implements ParameterFactory {

	public ParameterFactories() {
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
