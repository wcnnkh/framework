package run.soeasy.framework.net.resource;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.net.ContentDisposition;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.net.convert.support.AbstractNestedMessageConverter;
import run.soeasy.framework.util.io.FileMimeTypeUitls;
import run.soeasy.framework.util.io.InputStreamSource;
import run.soeasy.framework.util.io.MimeType;
import run.soeasy.framework.util.io.Resource;

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
