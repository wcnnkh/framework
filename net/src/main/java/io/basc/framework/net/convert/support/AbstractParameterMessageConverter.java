package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.service.ConversionService;
import io.basc.framework.core.convert.support.DefaultConversionService;
import io.basc.framework.core.execution.Parameter;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractParameterMessageConverter extends AbstractMessageConverter {
	@NonNull
	private ConversionService conversionService = DefaultConversionService.getInstance();

	protected abstract Object doRead(@NonNull ParameterDescriptor parameterDescriptor, @NonNull InputMessage message)
			throws IOException;

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		if (targetDescriptor instanceof ParameterDescriptor) {
			return doRead((ParameterDescriptor) targetDescriptor, message);
		}
		if (targetDescriptor.isRequired()) {
			throw new UnsupportedOperationException("required descriptor:" + targetDescriptor);
		}
		return null;
	}

	protected abstract void doWrite(@NonNull Parameter parameter, @NonNull OutputMessage message) throws IOException;

	@Override
	protected void doWrite(@NonNull Source source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		if (source instanceof Parameter) {
			doWrite((Parameter) source, message);
		}
	}

	protected abstract boolean isReadable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message message);

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (targetDescriptor instanceof ParameterDescriptor) {
			return isReadable((ParameterDescriptor) targetDescriptor, message)
					&& super.isReadable(targetDescriptor, message, contentType);
		}
		return false;
	}

	protected abstract boolean isWriteable(@NonNull ParameterDescriptor parameterDescriptor, @NonNull Message message);

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (sourceDescriptor instanceof ParameterDescriptor) {
			return isWriteable((ParameterDescriptor) sourceDescriptor, message)
					&& super.isWriteable(sourceDescriptor, message, contentType);
		}
		return false;
	}
}
