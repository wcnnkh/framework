package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.transform.property.PropertyDescriptor;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

@Getter
@Setter
public abstract class AbstractPropertyMessageConverter extends AbstractMessageConverter {
	@NonNull
	private ConversionService conversionService = SystemConversionService.getInstance();

	protected abstract Object doRead(@NonNull PropertyDescriptor parameterDescriptor, @NonNull InputMessage message)
			throws IOException;

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		if (targetDescriptor instanceof PropertyDescriptor) {
			return doRead((PropertyDescriptor) targetDescriptor, message);
		}
		if (targetDescriptor.isRequired()) {
			throw new UnsupportedOperationException("required descriptor:" + targetDescriptor);
		}
		return null;
	}

	protected abstract void doWrite(@NonNull PropertyAccessor parameter, @NonNull OutputMessage message)
			throws IOException;

	@Override
	protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		if (source instanceof PropertyAccessor) {
			doWrite((PropertyAccessor) source, message);
		}
	}

	protected abstract boolean isReadable(@NonNull PropertyDescriptor parameterDescriptor, @NonNull Message message);

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (targetDescriptor instanceof PropertyDescriptor) {
			return isReadable((PropertyDescriptor) targetDescriptor, message)
					&& super.isReadable(targetDescriptor, message, contentType);
		}
		return false;
	}

	protected abstract boolean isWriteable(@NonNull PropertyDescriptor parameterDescriptor, @NonNull Message message);

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (sourceDescriptor instanceof PropertyDescriptor) {
			return isWriteable((PropertyDescriptor) sourceDescriptor, message)
					&& super.isWriteable(sourceDescriptor, message, contentType);
		}
		return false;
	}
}
