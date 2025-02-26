package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.net.Response;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public abstract class AbstractBinaryMessageConverter<T> extends ObjectMessageConverter<T> {
	public AbstractBinaryMessageConverter(@NonNull Class<? extends T> requriedType) {
		super(requriedType);
	}

	protected abstract T parseObject(byte[] body, TargetDescriptor targetDescriptor, MimeType contentType,
			Message message) throws IOException;

	protected abstract byte[] toBinary(@NonNull Data<T> body, MediaType mediaType, @NonNull Message message)
			throws IOException;

	@Override
	protected T readObject(@NonNull TargetDescriptor targetDescriptor, MimeType contentType,
			@NonNull InputMessage request, @NonNull Response response) throws IOException {
		byte[] body = request.readAllBytes();
		return parseObject(body, targetDescriptor, contentType, response);
	}

	@Override
	protected void writeObject(Data<T> data, MediaType contentType, Request request, OutputMessage response)
			throws IOException {
		byte[] body = toBinary(data, contentType, request);
		if (body == null) {
			return;
		}

		if (response.getContentLength() < 0) {
			response.setContentLength(body.length);
		}
		response.getOutputStreamPipeline().optional().ifPresent((e) -> e.write(body));
	}
}
