package run.soeasy.framework.http.convert;

import java.io.IOException;
import java.util.List;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.execution.Parameter;
import run.soeasy.framework.core.execution.ParameterDescriptor;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.net.convert.support.AbstractParameterMessageConverter;
import run.soeasy.framework.util.ClassUtils;

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
