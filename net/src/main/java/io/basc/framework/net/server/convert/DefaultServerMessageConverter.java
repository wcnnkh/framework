package io.basc.framework.net.server.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.Value;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultServerMessageConverter extends ConfigurableServerMessageConverter {
	@NonNull
	private ServerMessageConverter groundServerMessageConverter = GlobalServerMessageConverter.getInstance();

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return super.isWriteable(typeDescriptor, contentType)
				|| groundServerMessageConverter.isWriteable(typeDescriptor, contentType);
	}

	@Override
	public boolean isReadable(ParameterDescriptor parameterDescriptor, Request request) {
		return super.isReadable(parameterDescriptor, request)
				|| groundServerMessageConverter.isReadable(parameterDescriptor, request);
	}

	@Override
	public Object readFrom(ParameterDescriptor parameterDescriptor, Request request, InputMessage inputMessage)
			throws IOException {
		if (super.isReadable(parameterDescriptor, request)) {
			return super.readFrom(parameterDescriptor, request, inputMessage);
		}
		return groundServerMessageConverter.readFrom(parameterDescriptor, request, inputMessage);
	}

	@Override
	public void writeTo(Value source, MimeType contentType, OutputMessage target) throws IOException {
		if (super.isWriteable(source.getTypeDescriptor(), contentType)) {
			super.writeTo(source, contentType, target);
		}
		groundServerMessageConverter.writeTo(source, contentType, target);
	}
}