package io.basc.framework.http.convert;

import java.io.IOException;
import java.util.List;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.support.AbstractParameterMessageConverter;
import io.basc.framework.util.ClassUtils;
import lombok.NonNull;

public class HeaderParameterMessageConverter extends AbstractParameterMessageConverter {
	@Override
	protected boolean isReadable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message request) {
		return true;
	}

	@Override
	protected boolean isWriteable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message response) {
		return !response.getHeaders().isReadyOnly();
	}

	@Override
	protected Object doRead(@NonNull ParameterDescriptor parameterDescriptor, @NonNull InputMessage message)
			throws IOException {
		Object value;
		if (ClassUtils.isMultipleValues(parameterDescriptor.getRequiredTypeDescriptor().getType())) {
			value = message.getHeaders().get(parameterDescriptor.getName());
		} else {
			value = message.getHeaders().getFirst(parameterDescriptor.getName());
		}

		if (value == null) {
			return null;
		}
		return getConversionService().convert(value, TypeDescriptor.forObject(value),
				parameterDescriptor.getRequiredTypeDescriptor());
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doWrite(@NonNull Parameter parameter, @NonNull OutputMessage message) throws IOException {
		if (ClassUtils.isMultipleValues(parameter.getTypeDescriptor().getType())) {
			List<String> values = (List<String>) getConversionService().convert(parameter,
					TypeDescriptor.collection(List.class, String.class));
			message.getHeaders().put(parameter.getName(), values);
		} else {
			String value = (String) getConversionService().convert(parameter, TypeDescriptor.valueOf(String.class));
			message.getHeaders().add(parameter.getName(), value);
		}
	}
}
