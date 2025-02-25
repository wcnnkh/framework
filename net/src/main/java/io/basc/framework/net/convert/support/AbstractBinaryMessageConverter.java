package io.basc.framework.net.convert.support;

import java.io.IOException;

import io.basc.framework.core.convert.Data;
import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.Message;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.Request;
import io.basc.framework.util.io.MimeType;
import lombok.NonNull;

public abstract class AbstractBinaryMessageConverter<T> extends ObjectMessageConverter<T> {
	public AbstractBinaryMessageConverter(@NonNull Class<? extends T> requriedType) {
		super(requriedType);
	}

	protected abstract T parseObject(byte[] body, TargetDescriptor targetDescriptor, MimeType contentType,
			Message message) throws IOException;

	protected abstract byte[] toBinary(Data<T> body, MediaType mediaType, Message message) throws IOException;

	@Override
	protected T readObject(TargetDescriptor targetDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		byte[] body = inputMessage.readAllBytes();
		return parseObject(body, targetDescriptor, contentType, inputMessage);
	}

	@Override
	protected void writeObject(Data<T> data, MediaType contentType, Request request, OutputMessage outputMessage)
			throws IOException {
		byte[] body = toBinary(data, contentType, outputMessage);
		if(body == null) {
			return ;
		}
		
		if (outputMessage.getContentLength() < 0) {
			outputMessage.setContentLength(body.length);
		}
		outputMessage.getOutputStreamPipeline().optional().ifPresent((e) -> e.write(body));
	}
}
