package io.basc.framework.net.client.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class DefaultClientMessageConverter extends ConfigurableClientMessageConverter {
	@NonNull
	private ClientMessageConverter groundClientMessageConverter = GlobalClientMessageConverter.getInstance();

	@Override
	public boolean isWriteable(ParameterDescriptor parameterDescriptor, Request request) {
		return super.isWriteable(parameterDescriptor, request)
				|| groundClientMessageConverter.isWriteable(parameterDescriptor, request);
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return super.isReadable(typeDescriptor, contentType)
				|| groundClientMessageConverter.isReadable(typeDescriptor, contentType);
	}

	@Override
	public void writeTo(Parameter parameter, Request request, OutputMessage outputMessage) throws IOException {
		if (super.isWriteable(parameter, request)) {
			super.writeTo(parameter, request, outputMessage);
		}

		groundClientMessageConverter.writeTo(parameter, request, outputMessage);
	}

	@Override
	public Object readFrom(TypeDescriptor typeDescriptor, InputMessage source) throws IOException {
		if (super.isReadable(typeDescriptor, source.getContentType())) {
			return super.readFrom(typeDescriptor, source);
		}
		return groundClientMessageConverter.readFrom(typeDescriptor, source);
	}
}
