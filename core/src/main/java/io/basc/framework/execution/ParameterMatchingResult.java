package io.basc.framework.execution;

import io.basc.framework.mapper.ParameterDescriptor;
import lombok.Data;

@Data
public class ParameterMatchingResult {
	private final ParameterDescriptor parameterDescriptor;
	private final Parameter parameter;
	private final boolean matchingSuccessful;

}
