package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Request;

public interface RequestReader {
	boolean isReadable(ParameterDescriptor parameterDescriptor, Request request);

	Object readFrom(ParameterDescriptor parameterDescriptor, Request request, InputMessage inputMessage)
			throws IOException;
}
