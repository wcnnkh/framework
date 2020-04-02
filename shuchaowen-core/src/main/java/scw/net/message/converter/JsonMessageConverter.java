package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;

import scw.json.JSONSupport;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public final class JsonMessageConverter extends
		AbstractMessageConverter<Object> {
	private static final long serialVersionUID = 1L;
	public static final MimeType JSON_ALL = new MimeType("application",
			"*+json");
	private JSONSupport jsonSupport;

	public JsonMessageConverter(JSONSupport jsonSupport) {
		addAll(Arrays
				.asList(MimeTypeUtils.APPLICATION_JSON, JSON_ALL, TEXT_ALL));
		this.jsonSupport = jsonSupport;
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		if (text == null) {
			return null;
		}
		return jsonSupport.parseObject(text, type);
	}

	@Override
	protected void writeInternal(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		String text = jsonSupport.toJSONString(body);
		if (text == null) {
			return;
		}
		writeTextBody(text, contentType, outputMessage);
	}

}
