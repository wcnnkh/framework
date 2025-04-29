package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.Readable;
import run.soeasy.framework.core.convert.Writeable;
import run.soeasy.framework.core.convert.value.ValueAccessor;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.Headers;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.MediaTypeRegistry;
import run.soeasy.framework.messaging.MediaTypes;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;
import run.soeasy.framework.messaging.convert.MessageConverter;

@Getter
@Setter
public abstract class AbstractMessageConverter implements MessageConverter {
	private final MediaTypeRegistry mediaTypeRegistry = new MediaTypeRegistry();

	protected abstract Object doRead(@NonNull Writeable targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException;

	protected abstract void doWrite(@NonNull ValueAccessor source, @NonNull OutputMessage message,
			@NonNull MediaType contentType) throws IOException;

	@Override
	public MediaTypes getSupportedMediaTypes() {
		return mediaTypeRegistry;
	}
	
	public static void writeHeader(Message inputMessage, OutputMessage outputMessage) throws IOException {
		long len = outputMessage.getContentLength();
		if (len >= 0) {
			outputMessage.setContentLength(len);
		}

		MediaType mediaType = inputMessage.getContentType();
		if (mediaType != null) {
			outputMessage.setContentType(mediaType);
		}

		Headers headers = inputMessage.getHeaders();
		if (headers != null) {
			for (Entry<String, List<String>> entry : headers.entrySet()) {
				for (String value : entry.getValue()) {
					outputMessage.getHeaders().add(entry.getKey(), value);
				}
			}
		}
	}

	@Override
	public boolean isReadable(@NonNull Writeable targetDescriptor, @NonNull Message message,
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
	public boolean isWriteable(@NonNull Readable sourceDescriptor, @NonNull Message message,
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
	public Object readFrom(@NonNull Writeable targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		MimeType contentTypeToUse = contentType;
		if (contentTypeToUse == null) {
			contentTypeToUse = mediaTypeRegistry.first();
		}

		return doRead(targetDescriptor, message, contentTypeToUse);
	}

	@Override
	public void writeTo(@NonNull ValueAccessor source, @NonNull OutputMessage message, MediaType contentType)
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
