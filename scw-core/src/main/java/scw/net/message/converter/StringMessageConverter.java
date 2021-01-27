package scw.net.message.converter;

import java.io.IOException;

import scw.core.ResolvableType;
import scw.io.IOUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.value.StringValue;

public class StringMessageConverter extends AbstractMessageConverter<Object> {

	public StringMessageConverter() {
		supportMimeTypes.add(MimeTypeUtils.TEXT_PLAIN, TEXT_ALL);
		setSupportBytes(true);
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(ResolvableType type, InputMessage inputMessage) throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		if (type.getRawClass() == byte[].class) {
			return text.getBytes(getCharset(inputMessage));
		}

		StringValue value = new StringValue(text);
		value.setJsonSupport(getJsonSupport());
		return value.getAsObject(type);
	}

	@Override
	protected void writeInternal(ResolvableType type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		if (body instanceof byte[]) {
			IOUtils.write((byte[]) body, outputMessage.getBody());
		} else {
			writeTextBody(body.toString(), contentType, outputMessage);
		}
	}
}
