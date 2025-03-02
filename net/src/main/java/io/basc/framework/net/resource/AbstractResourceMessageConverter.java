package io.basc.framework.net.resource;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.ContentDisposition;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.support.AbstractNestedMessageConverter;
import io.basc.framework.util.io.FileMimeTypeUitls;
import io.basc.framework.util.io.InputStreamSource;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.io.Resource;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class AbstractResourceMessageConverter<T extends Resource> extends AbstractNestedMessageConverter {
	@NonNull
	private String contentDispositionName = "resource";
	@NonNull
	private final Class<? extends T> resourceClass;

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		return targetDescriptor.getRequiredTypeDescriptor().getType() == Resource.class
				&& super.isReadable(targetDescriptor, message, contentType);
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		return Resource.class.isAssignableFrom(sourceDescriptor.getTypeDescriptor().getType())
				&& super.isWriteable(sourceDescriptor, message, contentType);
	}

	@Override
	protected void doWrite(@NonNull Source source, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		Resource resource = source.getAsObject(Resource.class);
		if (!resource.exists() || !resource.isReadable()) {
			// TODO 可以加日志
			return;
		}

		MimeType mimeType = FileMimeTypeUitls.getMimeType(resource);
		ContentDisposition contentDisposition = ContentDisposition.builder("form-data")
				.name(getContentDispositionName()).filename(resource.getName()).build();
		message.getHeaders().setContentDisposition(contentDisposition);
		message.setContentType(new MediaType(mimeType));
		resource.transferTo(message);
	}

	@Override
	protected Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		return readToResource(message, message, targetDescriptor);
	}

	protected abstract T readToResource(@NonNull Message message, @NonNull InputStreamSource<?> source,
			@NonNull TargetDescriptor targetDescriptor) throws IOException;
}
