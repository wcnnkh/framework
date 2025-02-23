package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;

import io.basc.framework.core.Constants;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class StringMessageConverter<T> extends ObjectMessageConverter<T> {
	@NonNull
	private Charset charset = Constants.UTF_8;

	protected Charset getCharset(Message message, MimeType contentType) {
		Charset charset = message.getCharset();
		if (charset == null) {
			for (MediaType mimeType : getMediaTypeRegistry()) {
				if (mimeType.getCharset() == null) {
					continue;
				}

				if (mimeType.isCompatibleWith(contentType)) {
					return mimeType.getCharset();
				}
			}
		}
		return getCharset();
	}

	protected abstract T parseObject(String body, TypeDescriptor targetTypeDescriptor);

	@Override
	protected T read(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		Charset charset = getCharset(inputMessage, contentType);
		String body = inputMessage.toReaderFactory(charset).readAllCharacters();
		return parseObject(body, typeDescriptor);
	}

	protected abstract String toString(TypeDescriptor typeDescriptor, T body, MimeType contentType);

	@Override
	protected void write(TypeDescriptor typeDescriptor, T body, MediaType contentType, OutputMessage outputMessage)
			throws IOException {
		MediaType contentTypeToUse = contentType;
		Charset charset = getCharset(outputMessage, contentType);
		if (contentTypeToUse.getCharset() == null) {
			if (!contentTypeToUse.isWildcardType() && !contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = new MediaType(contentTypeToUse, charset);
			}
		}
		outputMessage.setContentType(contentTypeToUse);
		String content = toString(typeDescriptor, body, contentTypeToUse);
		byte[] array = content.getBytes(contentType.getCharset());
		if (outputMessage.getContentLength() < 0) {
			outputMessage.setContentLength(array.length);
		}
		outputMessage.getOutputStreamPipeline().optional().ifPresent((e) -> e.write(array));
	}
}
