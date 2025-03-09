package io.basc.framework.net.convert.uri;

import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.uri.UriComponents;
import io.basc.framework.net.uri.UriComponentsBuilder;
import lombok.NonNull;

public interface UriParameterConverter {
	boolean canConvert(@NonNull ParameterDescriptor parameterDescriptor);

	Object readFrom(@NonNull ParameterDescriptor parameterDescriptor, @NonNull UriComponents request);

	void writeTo(@NonNull Parameter parameter, @NonNull UriComponentsBuilder response);
}
