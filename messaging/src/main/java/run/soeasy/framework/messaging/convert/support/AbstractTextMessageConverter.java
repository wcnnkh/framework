package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

@Getter
@Setter
public abstract class AbstractTextMessageConverter<T> extends AbstractBinaryMessageConverter<T> {
	@NonNull
	private Charset defaultCharset = StandardCharsets.UTF_8;

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
	protected T parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException {
		Charset charset = getCharset(contentType, message);
		String text = new String(body, charset);
		return parseObject(text, targetDescriptor);
	}

	@Override
	protected byte[] toBinary(@NonNull TypedData<T> body, @NonNull Message message, MediaType mediaType) throws IOException {
		Charset charset = getCharset(mediaType, message);
		String text = toString(body, mediaType, charset);
		return text.getBytes(charset);
	}

	@Override
	protected void writeObject(@NonNull TypedData<T> data, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		MediaType contentTypeToUse = contentType;
		Charset charset = getCharset(contentType, message);
		if (contentTypeToUse.getCharset() == null) {
			if (!contentTypeToUse.isWildcardType() && !contentTypeToUse.isWildcardSubtype()) {
				contentTypeToUse = new MediaType(contentTypeToUse, charset);
			}
		}

		if (message.getContentType() != null) {
			message.setContentType(contentTypeToUse);
		}
		super.writeObject(data, message, contentTypeToUse);
	}

	protected abstract T parseObject(String body, TargetDescriptor targetDescriptor) throws IOException;

	protected abstract String toString(TypedData<T> body, MediaType contentType, Charset charset) throws IOException;
}
