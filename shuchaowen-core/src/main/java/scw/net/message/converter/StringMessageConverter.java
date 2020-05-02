package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.util.value.StringValue;

public class StringMessageConverter extends AbstractMessageConverter<Object> {

	public StringMessageConverter() {
		supportMimeTypes.add(MimeTypeUtils.TEXT_PLAIN, TEXT_ALL);
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		StringValue value = new StringValue(text);
		value.setJsonSupport(getJsonSupport());
		return value.getAsObject(type);
	}

	@Override
	protected void writeInternal(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		writeTextBody(body.toString(), contentType, outputMessage);
	}
}
