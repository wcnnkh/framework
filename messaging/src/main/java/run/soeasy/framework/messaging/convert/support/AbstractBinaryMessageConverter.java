package run.soeasy.framework.messaging.convert.support;

import java.io.IOException;

import lombok.NonNull;
import run.soeasy.framework.core.convert.value.ConvertingData;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.io.MimeType;
import run.soeasy.framework.messaging.InputMessage;
import run.soeasy.framework.messaging.MediaType;
import run.soeasy.framework.messaging.Message;
import run.soeasy.framework.messaging.OutputMessage;

public abstract class AbstractBinaryMessageConverter<T> extends ObjectMessageConverter<T> {
	public AbstractBinaryMessageConverter(@NonNull Class<? extends T> requriedType) {
		super(requriedType);
	}

	protected abstract T parseObject(byte[] body, @NonNull TargetDescriptor targetDescriptor, @NonNull Message message,
			MimeType contentType) throws IOException;

	protected abstract byte[] toBinary(@NonNull ConvertingData<T> body, @NonNull Message message, MediaType mediaType)
			throws IOException;

	@Override
	protected T readObject(@NonNull TargetDescriptor targetDescriptor, @NonNull InputMessage message,
			MimeType contentType) throws IOException {
		byte[] body = message.readAllBytes();
		return parseObject(body, targetDescriptor, message, contentType);
	}

	@Override
	protected void writeObject(@NonNull ConvertingData<T> data, @NonNull OutputMessage message, @NonNull MediaType contentType)
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
