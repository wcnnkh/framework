package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.property.ParameterAccessor;
import run.soeasy.framework.core.convert.property.ParameterDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

@Getter
@Setter
public abstract class AbstractParameterMessageConverter extends AbstractMessageConverter {
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

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

	protected abstract void doWrite(@NonNull ParameterAccessor parameter, @NonNull OutputMessage message) throws IOException;

	@Override
	protected void doWrite(@NonNull ValueAccessor source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		if (source instanceof ParameterAccessor) {
			doWrite((ParameterAccessor) source, message);
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
