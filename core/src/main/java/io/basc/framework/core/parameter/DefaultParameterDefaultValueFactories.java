package io.basc.framework.core.parameter;

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
