package io.basc.framework.net.client.convert;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.execution.param.ParameterDescriptor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;

public class ConfigurableClientMessageConverter extends ConfigurableServices<ClientMessageConverter>
		implements ClientMessageConverter {
	private static Logger logger = LoggerFactory.getLogger(ConfigurableClientMessageConverter.class);

	public ConfigurableClientMessageConverter() {
		setServiceClass(ClientMessageConverter.class);
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
		return getServices().anyMatch((e) -> e.isReadable(typeDescriptor, contentType));
	}

	@Override
	public Object readFrom(TypeDescriptor typeDescriptor, InputMessage source) throws IOException {
		for (ClientMessageConverter converter : getServices()) {
			if (converter.isReadable(typeDescriptor, source.getContentType())) {
				return converter.readFrom(typeDescriptor, source);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("not support read type={}, contentType={}", typeDescriptor, source.getContentType());
		}
		throw new UnsupportedException("not support read type " + typeDescriptor.getType());
	}

	@Override
	public boolean isWriteable(ParameterDescriptor parameterDescriptor, Request request) {
		return getServices().anyMatch((e) -> e.isWriteable(parameterDescriptor, request));
	}

	@Override
	public void writeTo(Parameter parameter, Request request, OutputMessage outputMessage) throws IOException {
		for (ClientMessageConverter converter : getServices()) {
			if (converter.isWriteable(parameter, request)) {
				converter.writeTo(parameter, request, outputMessage);
			}
		}
	}

}
