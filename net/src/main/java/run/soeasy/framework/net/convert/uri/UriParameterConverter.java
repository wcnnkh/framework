package run.soeasy.framework.net.convert.uri;

import lombok.NonNull;
import run.soeasy.framework.core.execution.Parameter;
import run.soeasy.framework.core.execution.ParameterDescriptor;
import run.soeasy.framework.net.uri.UriComponents;
import run.soeasy.framework.net.uri.UriComponentsBuilder;

public interface UriParameterConverter {
	boolean canConvert(@NonNull ParameterDescriptor parameterDescriptor);

	Object readFrom(@NonNull ParameterDescriptor parameterDescriptor, @NonNull UriComponents request);

	void writeTo(@NonNull Parameter parameter, @NonNull UriComponentsBuilder response);
}
