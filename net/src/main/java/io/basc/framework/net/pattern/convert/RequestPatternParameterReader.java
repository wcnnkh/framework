package io.basc.framework.net.pattern.convert;

import java.io.IOException;

import io.basc.framework.core.execution.param.ParameterDescriptor;
import io.basc.framework.core.execution.param.Parameters;

public interface RequestPatternParameterReader {
	boolean isReadable(ParameterDescriptor parameterDescriptor);

	Object readFrom(ParameterDescriptor parameterDescriptor, Parameters parameters) throws IOException;
}
