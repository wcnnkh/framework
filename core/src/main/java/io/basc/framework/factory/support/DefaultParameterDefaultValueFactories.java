package io.basc.framework.factory.support;

import io.basc.framework.core.parameter.ParameterDefaultValueFactory;
import io.basc.framework.core.parameter.ParameterDescriptor;
import io.basc.framework.core.parameter.ParameterFactories;

public class DefaultParameterDefaultValueFactories extends ParameterFactories {

	@Override
	public boolean isAccept(ParameterDescriptor parameterDescriptor) {
		if (super.isAccept(parameterDescriptor)) {
			return true;
		}
		return ParameterDefaultValueFactory.INSTANCE.isAccept(parameterDescriptor);
	}

	@Override
	public Object getParameter(ParameterDescriptor parameterDescriptor) {
		if (super.isAccept(parameterDescriptor)) {
			return super.getParameter(parameterDescriptor);
		}

		if (ParameterDefaultValueFactory.INSTANCE.isAccept(parameterDescriptor)) {
			return ParameterDefaultValueFactory.INSTANCE.getParameter(parameterDescriptor);
		}
		return null;
	}
}
