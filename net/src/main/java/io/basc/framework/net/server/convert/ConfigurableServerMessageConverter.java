package io.basc.framework.net.server.convert;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableServerMessageConverter extends ConfigurableServices<ServerMessageConverter>
		implements ServerMessageConverter {
	private static Logger logger = LogManager.getLogger(ServerMessageConverter.class);

	public ConfigurableServerMessageConverter() {
		setServiceClass(ServerMessageConverter.class);
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return anyMatch((e) -> e.isWriteable(typeDescriptor, contentType));
	}

	@Override
	public void writeTo(Value source, MimeType contentType, OutputMessage target) throws IOException {
		for (ServerMessageConverter converter : this) {
			if (converter.isWriteable(source.getTypeDescriptor(), contentType)) {
				converter.writeTo(source, contentType, target);
				return;
			}
		}
	}

	@Override
	public boolean isReadable(ParameterDescriptor parameterDescriptor, Request request) {
		return anyMatch((e) -> e.isReadable(parameterDescriptor, request));
	}

	@Override
	public Object readFrom(ParameterDescriptor parameterDescriptor, Request request, InputMessage inputMessage)
			throws IOException {
		for (ServerMessageConverter converter : this) {
			if (converter.isReadable(parameterDescriptor, request)) {
				return converter.readFrom(parameterDescriptor, request, inputMessage);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not support read parameter={}, contentType={}", parameterDescriptor,
					request.getContentType());
		}
		throw new UnsupportedOperationException("not support read parameter " + parameterDescriptor);
	}

}
