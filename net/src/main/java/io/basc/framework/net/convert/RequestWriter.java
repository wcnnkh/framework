package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;

public interface RequestWriter {
	boolean isWriteable(ParameterDescriptor parameterDescriptor, Request request);

	void writeTo(Parameter parameter, Request request, OutputMessage outputMessage) throws IOException;
}
