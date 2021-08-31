package io.basc.framework.core.parameter;

import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;

/**
 * 获取默认值
 * 
 * @author shuchaowen
 *
 */
public class ParameterDefaultValueFactory extends ConfigurableServices<ParameterFactory> implements ParameterFactory {

	public ParameterDefaultValueFactory() {
		super(ParameterFactory.class);
	}

	public ParameterDefaultValueFactory(ServiceLoaderFactory serviceLoaderFactory) {
		this();
		configure(serviceLoaderFactory);
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
		return parameterDescriptor.getDefaultValue();
	}

}
