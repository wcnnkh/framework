package io.basc.framework.messageing.handler;

import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.messageing.Message;

public interface HandleMessageConverter {
	@Nullable
	Object convert(Message<?> message, ParameterDescriptor parameterDescriptor);
}
