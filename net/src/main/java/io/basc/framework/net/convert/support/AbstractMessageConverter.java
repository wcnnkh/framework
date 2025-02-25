package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.SourceDescriptor;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.MediaTypeRegistry;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMessageConverter implements MessageConverter {
	private final MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

	protected abstract Object doRead(TargetDescriptor targetDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException;

	protected abstract void doWrite(Source source, MediaType contentType, Request request, OutputMessage outputMessage)
			throws IOException;

	@Override
	public MediaTypes getSupportedMediaTypes() {
		return mediaTypeRegistry;
	}

	@Override
	public boolean isReadable(@NonNull TargetDescriptor targetDescriptor, MimeType contentType) {
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
	public boolean isWriteable(@NonNull SourceDescriptor sourceDescriptor, MimeType contentType) {
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
	public final Object readFrom(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage inputMessage) throws IOException {
		MimeType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.first();
		}

		return doRead(targetDescriptor, contentTypeToUse, inputMessage);
	}

	@Override
	public final void writeTo(@NonNull Source source, MediaType contentType, @NonNull Request request,
			@NonNull OutputMessage outputMessage) throws IOException {
		MediaType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.filter((e) -> !e.isWildcardType() && !e.isWildcardSubtype()).first();
		}

		if (contentTypeToUse != null) {
			outputMessage.setContentType(contentTypeToUse);
		}

		doWrite(source, contentTypeToUse, request, outputMessage);
	}
}
