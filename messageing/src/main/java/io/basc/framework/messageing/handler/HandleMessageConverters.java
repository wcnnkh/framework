package io.basc.framework.messageing.handler;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.convert.transform.ParameterDescriptor;
import io.basc.framework.messageing.Message;

public class HandleMessageConverters extends ConfigurableServices<HandleMessageConverter>
		implements HandleMessageConverter {

	public HandleMessageConverters() {
		super(HandleMessageConverter.class);
	}

	@Override
	public Object convert(Message<?> message, ParameterDescriptor parameterDescriptor) {
		for (HandleMessageConverter converter : getServices()) {
			Object value = converter.convert(message, parameterDescriptor);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

}
