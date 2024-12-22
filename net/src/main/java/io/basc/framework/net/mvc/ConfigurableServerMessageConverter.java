package io.basc.framework.net.mvc;

import java.io.IOException;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.server.convert.ServerMessageConverter;

public class ConfigurableServerMessageConverter extends ConfigurableServices<ServerMessageConverter>
		implements ServerMessageConverter {

	public ConfigurableServerMessageConverter() {
		setServiceClass(ServerMessageConverter.class);
	}

	@Override
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
		for (ServerMessageConverter converter : getServices()) {
			if (converter.isWriteable(typeDescriptor, contentType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void writeTo(Value source, MimeType contentType, OutputMessage target) throws IOException {
		for (ServerMessageConverter converter : getServices()) {
			if (converter.isWriteable(source.getTypeDescriptor(), contentType)) {
				converter.writeTo(source, contentType, target);
				return;
			}
		}
	}

	@Override
	public boolean isReadable(ParameterDescriptor parameterDescriptor, Request request) {
		for (ServerMessageConverter converter : getServices()) {
			if (converter.isReadable(parameterDescriptor, request)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object readFrom(ParameterDescriptor parameterDescriptor, Request request, InputMessage inputMessage)
			throws IOException {
		for (ServerMessageConverter converter : getServices()) {
			if (converter.isReadable(parameterDescriptor, request)) {
				return converter.readFrom(parameterDescriptor, request, inputMessage);
			}
		}
		// TODO 定义一个异常信息
		throw new UnsupportedException("");
	}

}
