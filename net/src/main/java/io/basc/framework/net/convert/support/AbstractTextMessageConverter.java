package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;

import io.basc.framework.core.Constants;
import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.util.io.MimeType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractTextMessageConverter<T> extends AbstractBinaryMessageConverter<T> {
	@NonNull
	private Charset defaultCharset = Constants.UTF_8;

	public AbstractTextMessageConverter(@NonNull Class<? extends T> requriedType) {
		super(requriedType);
	}

	protected final Charset getCharset(MimeType contentType, Message message) {
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
		return getDefaultCharset();
	}

	@Override
	protected final T parseObject(byte[] body, TargetDescriptor targetDescriptor, MimeType contentType, Message message)
			throws IOException {
		Charset charset = getCharset(contentType, message);
		String text = new String(body, charset);
		return parseObject(text, targetDescriptor);
	}

	@Override
	protected final byte[] toBinary(Data<T> body, MediaType mediaType, Message message) throws IOException {
		Charset charset = getCharset(mediaType, message);
		String text = toString(body, mediaType, charset);
		return text.getBytes(charset);
	}

	@Override
	protected final void writeObject(Data<T> data, MediaType contentType, Request request, OutputMessage outputMessage)
			throws IOException {
		MediaType contentTypeToUse = contentType;
		Charset charset = getCharset(contentType, outputMessage);
		if (contentTypeToUse.getCharset() == null) {
			if (!contentTypeToUse.isWildcardType() && !contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = new MediaType(contentTypeToUse, charset);
			}
		}
		outputMessage.setContentType(contentTypeToUse);
		super.writeObject(data, contentTypeToUse, request, outputMessage);
	}

	protected abstract T parseObject(String body, TargetDescriptor targetDescriptor) throws IOException;

	protected abstract String toString(Data<T> body, MediaType contentType, Charset charset) throws IOException;
}
