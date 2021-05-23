package scw.mvc.convert;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.core.parameter.ParameterDescriptor;
import scw.web.ServerHttpRequest;
import scw.web.ServerHttpResponse;

public interface ChannelMessagelConverter {
	boolean canRead(ParameterDescriptor parameterDescriptor);

	Object read(ParameterDescriptor parameterDescriptor,
			ServerHttpRequest request) throws IOException,
			ChannelMessagelConverterException;

	boolean canWrite(TypeDescriptor type, Object body);

	void write(TypeDescriptor type, Object body, ServerHttpRequest request,
			ServerHttpResponse response) throws IOException,
			ChannelMessagelConverterException;
}
