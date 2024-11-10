package io.basc.framework.net.server.convert;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

public class ConfigurableServerMessageConverter extends ConfigurableServices<ServerMessageConverter>
		implements ServerMessageConverter {
	private static Logger logger = LogManager.getLogger(ServerMessageConverter.class);

	public ConfigurableServerMessageConverter() {
		setServiceClass(ServerMessageConverter.class);
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return getServices().anyMatch((e) -> e.isWriteable(typeDescriptor, contentType));
	}

	@Override
	public void writeTo(ValueWrapper source, MimeType contentType, OutputMessage target) throws IOException {
		for (ServerMessageConverter converter : getServices()) {
			if (converter.isWriteable(source.getTypeDescriptor(), contentType)) {
				converter.writeTo(source, contentType, target);
				return;
			}
		}
	}

	@Override
	public boolean isReadable(ParameterDescriptor parameterDescriptor, Request request) {
		return getServices().anyMatch((e) -> e.isReadable(parameterDescriptor, request));
	}

	@Override
	public Object readFrom(ParameterDescriptor parameterDescriptor, Request request, InputMessage inputMessage)
			throws IOException {
		for (ServerMessageConverter converter : getServices()) {
			if (converter.isReadable(parameterDescriptor, request)) {
				return converter.readFrom(parameterDescriptor, request, inputMessage);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not support read parameter={}, contentType={}", parameterDescriptor,
					request.getContentType());
		}
		throw new UnsupportedException("not support read parameter " + parameterDescriptor);
	}

}
