package io.basc.framework.messageing.handler;

import io.basc.framework.lang.Nullable;
import io.basc.framework.messageing.Message;
import io.basc.framework.value.ParameterDescriptor;

public interface HandleMessageConverter {
	@Nullable
	Object convert(Message<?> message, ParameterDescriptor parameterDescriptor);
}
