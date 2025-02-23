package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.MediaTypeRegistry;
import io.basc.framework.net.MediaTypes;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.MessageConverter;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMessageConverter implements MessageConverter {
	private final MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

	protected abstract Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException;

	protected abstract void doWrite(Source source, MediaType contentType, OutputMessage outputMessage)
			throws IOException;

	protected MediaType getContentType(TypeDescriptor type, Message message) throws IOException {
		MediaType mediaType = mediaTypeRegistry.first();
		if (mediaType.isWildcardType() || mediaType.isWildcardSubtype()) {
			return null;
		}
		return mediaType;
	}

	@Override
	public MediaTypes getSupportedMediaTypes() {
		return mediaTypeRegistry;
	}

	@Override
	public boolean isReadable(TypeDescriptor typeDescriptor, MimeType contentType) {
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
	public boolean isWriteable(TypeDescriptor typeDescriptor, MimeType contentType) {
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
	public final Object readFrom(TypeDescriptor typeDescriptor, InputMessage inputMessage) throws IOException {
		MimeType contentTypeToUse = inputMessage.getContentType();
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.first();
		}

		return doRead(typeDescriptor, contentTypeToUse, inputMessage);
	}

	@Override
	public final void writeTo(Source source, MediaType contentType, OutputMessage outputMessage) throws IOException {
		MediaType contentTypeToUse = contentType;
		if (contentType == null) {
			contentTypeToUse = outputMessage.getContentType();
		} else if (outputMessage.getContentType() == null) {
			if (contentTypeToUse == null || contentTypeToUse.isWildcardType() || contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = getContentType(source.getTypeDescriptor(), outputMessage);
			}
			outputMessage.setContentType(contentTypeToUse);
		}
		doWrite(source, contentTypeToUse, outputMessage);
	}
}
