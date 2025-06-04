package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.Entity;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

@RequiredArgsConstructor
@Getter
public abstract class AbstractEntityMessageConverter<T extends Entity<?>> extends AbstractNestedMessageConverter {
	@NonNull
	private final Class<? extends T> entityClass;

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		return targetDescriptor.getRequiredTypeDescriptor().getType().isAssignableFrom(entityClass)
				&& super.isReadable(targetDescriptor, message, contentType);
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		return Entity.class.isAssignableFrom(sourceDescriptor.getReturnTypeDescriptor().getType())
				&& !message.getHeaders().isReadyOnly() && super.isWriteable(sourceDescriptor, message, contentType);
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		TypeDescriptor typeDescriptor = targetDescriptor.getRequiredTypeDescriptor();
		if (typeDescriptor.isGeneric()) {
			typeDescriptor = typeDescriptor.getNested(1);
		} else {
			typeDescriptor = TypeDescriptor.valueOf(Object.class);
		}

		Object value = getMessageConverter().readFrom(() -> targetDescriptor.getRequiredTypeDescriptor(), message,
				contentType);
		return readToEntity(TypedValue.of(value, typeDescriptor), message);
	}

	protected abstract T readToEntity(@NonNull TypedValue body, @NonNull InputMessage message);

	@Override
	protected void doWrite(@NonNull TypedValue source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		Entity<?> entity = (Entity<?>) source;
		writeHeader(entity, message);
		TypedData<?> entityBody = entity.getBody();
		if (entityBody != null) {
			getMessageConverter().writeTo(entityBody.value(), message, contentType);
		}
	}
}
