package io.basc.framework.messageing.convert;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.lang.Nullable;
import io.basc.framework.messageing.Message;
import io.basc.framework.messageing.MessageHeaders;

public interface MessageConverter {
	@Nullable
	Object fromMessage(Message<?> message, TypeDescriptor payloadTypeDescriptor);

	@Nullable
	Message<?> toMessage(Object payload, TypeDescriptor payloadTypeDescriptor, @Nullable MessageHeaders headers);
}
