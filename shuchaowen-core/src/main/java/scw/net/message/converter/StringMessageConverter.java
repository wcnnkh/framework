package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.core.utils.TypeUtils;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public class StringMessageConverter extends AbstractTextMessageConverter {

	@Override
	protected boolean canRead(Type type) {
		return TypeUtils.isAssignableFrom(type, String.class);
	}

	@Override
	protected boolean canRead(MimeType contentType) {
		return true;
	}

	@Override
	protected boolean canWrite(Object body) {
		return body instanceof String;
	}

	@Override
	protected boolean canWrite(MimeType contentType) {
		return true;
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage)
			throws IOException {
		return read(inputMessage);
	}

	@Override
	protected void writeInternal(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException {
		write((String) body, contentType, outputMessage);
	}

}
