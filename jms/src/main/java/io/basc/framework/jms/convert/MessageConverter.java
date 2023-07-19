package io.basc.framework.jms.convert;

import javax.jms.Message;

import io.basc.framework.mapper.ParameterDescriptor;

public interface MessageConverter {
	boolean canRead(ParameterDescriptor parameterDescriptor);

	Object read(Message message, ParameterDescriptor parameterDescriptor);

	boolean canWrite(ParameterDescriptor parameterDescriptor);
	
	Object write(Message message, ParameterDescriptor parameterDescriptor);
}
