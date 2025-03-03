package io.basc.framework.http.jaxrs;

import java.io.IOException;

import javax.ws.rs.HeaderParam;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.http.convert.HeaderParameterMessageConverter;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.StringUtils;
import lombok.NonNull;

public class JaxrsHeaderParamMessageConverter extends HeaderParameterMessageConverter {

	private String getHeaderName(String name, TypeDescriptor typeDescriptor) {
		HeaderParam headerParam = typeDescriptor.getAnnotation(HeaderParam.class);
		if (headerParam == null) {
			return null;
		}
		return StringUtils.isEmpty(headerParam.value()) ? name : headerParam.value();
	}

	@Override
	protected boolean isReadable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message request) {
		return StringUtils.isNotEmpty(
				getHeaderName(parameterDescriptor.getName(), parameterDescriptor.getRequiredTypeDescriptor()))
				&& super.isReadable(parameterDescriptor, request);
	}

	@Override
	protected Object doRead(@NonNull ParameterDescriptor parameterDescriptor, @NonNull InputMessage message)
			throws IOException {
		String name = getHeaderName(parameterDescriptor.getName(), parameterDescriptor.getRequiredTypeDescriptor());
		return super.doRead(parameterDescriptor.rename(name), message);
	}

	@Override
	protected boolean isWriteable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message response) {
		return StringUtils
				.isNotEmpty(getHeaderName(parameterDescriptor.getName(), parameterDescriptor.getTypeDescriptor()))
				&& super.isWriteable(parameterDescriptor, response);
	}

	@Override
	protected void doWrite(@NonNull Parameter parameter, @NonNull OutputMessage message) throws IOException {
		String name = getHeaderName(parameter.getName(), parameter.getTypeDescriptor());
		super.doWrite(parameter.rename(name), message);
	}
}
