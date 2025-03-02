package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public abstract class AbstractBinaryMessageConverter<T> extends ObjectMessageConverter<T> {
	public AbstractBinaryMessageConverter(@NonNull Class<? extends T> requriedType) {
		super(requriedType);
	}

	protected abstract T parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException;

	protected abstract byte[] toBinary(@NonNull Data<T> body, @NonNull Message message, MediaType mediaType)
			throws IOException;

	@Override
	protected T readObject(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		byte[] body = message.readAllBytes();
		return parseObject(body, targetDescriptor, message, contentType);
	}

	@Override
	protected void writeObject(@NonNull Data<T> data, @NonNull OutputMessage message, @NonNull MediaType contentType)
			throws IOException {
		byte[] body = toBinary(data, message, contentType);
		if (body == null) {
			return;
		}

		if (message.getContentLength() < 0) {
			message.setContentLength(body.length);
		}
		message.getOutputStreamPipeline().optional().ifPresent((e) -> e.write(body));
	}
}
