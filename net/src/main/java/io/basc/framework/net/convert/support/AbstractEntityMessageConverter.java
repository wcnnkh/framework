package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.Entity;
import io.basc.framework.net.InetUtils;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

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
		return Entity.class.isAssignableFrom(sourceDescriptor.getTypeDescriptor().getType())
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
		return readToEntity(Source.of(value, typeDescriptor), message);
	}

	protected abstract T readToEntity(@NonNull Source body, @NonNull InputMessage message);

	@Override
	protected void doWrite(@NonNull Source source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		Entity<?> entity = (Entity<?>) source;
		InetUtils.writeHeader(entity, message);
		Data<?> entityBody = entity.getBody();
		if (entityBody != null) {
			getMessageConverter().writeTo(entityBody.any(), message, contentType);
		}
	}
}
