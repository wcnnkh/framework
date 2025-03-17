package run.soeasy.framework.net.convert.support;

import java.io.IOException;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.TargetDescriptor;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.MediaType;
import run.soeasy.framework.net.MediaTypeRegistry;
import run.soeasy.framework.net.MediaTypes;
import run.soeasy.framework.net.Message;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.net.convert.MessageConverter;
import run.soeasy.framework.util.io.MimeType;

@Getter
@Setter
public abstract class AbstractMessageConverter implements MessageConverter {
	private final MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

	protected abstract Object doRead(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException;

	protected abstract void doWrite(@NonNull Source source, @NonNull OutputMessage message,
			@NonNull MediaType contentType) throws IOException;

	@Override
	public MediaTypes getSupportedMediaTypes() {
		return mediaTypeRegistry;
	}

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (contentType == null) {
			return true;
		}

		for (MimeType mimeType : mediaTypeRegistry) {
			if (mimeType.includes(contentType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, @NonNull Message message,
			MimeType contentType) {
		if (contentType == null || MediaType.ALL.equalsTypeAndSubtype(contentType)) {
			return true;
		}

		for (MimeType mimeType : mediaTypeRegistry) {
			if (mimeType.isCompatibleWith(contentType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object readFrom(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		MimeType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.first();
		}

		return doRead(targetDescriptor, message, contentTypeToUse);
	}

	@Override
	public void writeTo(@NonNull Source source, @NonNull OutputMessage message, MediaType contentType)
			throws IOException {
		MediaType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.filter((e) -> !e.isWildcardType() && !e.isWildcardSubtype()).first();
		}

		if (contentTypeToUse != null && message.getContentType() == null) {
			message.setContentType(contentTypeToUse);
		}

		doWrite(source, message, contentTypeToUse);
	}

}
