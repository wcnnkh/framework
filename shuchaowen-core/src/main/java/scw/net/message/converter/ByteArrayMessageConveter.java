package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.core.utils.TypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.mime.MimeType;

public class ByteArrayMessageConveter extends AbstractMessageConverter {

	@Override
	protected boolean canRead(Type type) {
		return TypeUtils.isAssignableFrom(type, byte[].class);
	}

	@Override
	protected boolean canRead(MimeType contentType) {
		return true;
	}

	@Override
	protected boolean canWrite(Object body) {
		return TypeUtils.isAssignableFrom(body.getClass(), byte[].class);
	}

	@Override
	protected boolean canWrite(MimeType contentType) {
		return true;
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage) throws IOException {
		return inputMessage.toByteArray();
	}

	@Override
	protected void writeInternal(Object body, MimeType contentType, OutputMessage outputMessage) throws IOException {
		outputMessage.getBody().write((byte[]) body);
	}

}
