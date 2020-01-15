package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public abstract class AbstractMessageConverter implements MessageConverter {

	protected abstract boolean canRead(Type type);

	protected abstract boolean canRead(MimeType contentType);

	protected abstract boolean canWrite(Object body);

	protected abstract boolean canWrite(MimeType contentType);

	public Object read(Type type, InputMessage inputMessage, MessageConverterChain chain) throws IOException {
		if (canRead(type) && canRead(inputMessage.getContentType())) {
			return readInternal(type, inputMessage);
		} else {
			return chain.read(type, inputMessage);
		}
	}

	protected abstract Object readInternal(Type type, InputMessage inputMessage) throws IOException;

	public void write(Object body, MimeType contentType, OutputMessage outputMessage, MessageConverterChain chain)
			throws IOException {
		if (canWrite(body) && canWrite(contentType)) {
			if (contentType != null && outputMessage.getContentType() != null) {
				outputMessage.setContentType(contentType);
			}
			writeInternal(body, contentType, outputMessage);
		} else {
			chain.write(body, contentType, outputMessage);
		}
	}

	protected abstract void writeInternal(Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException;
}
