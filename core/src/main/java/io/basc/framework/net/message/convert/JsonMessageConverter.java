package io.basc.framework.net.message.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.json.JSONSupport;
import io.basc.framework.mapper.MapperUtils;
import io.basc.framework.mapper.ToMap;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;

import java.io.IOException;

public final class JsonMessageConverter extends AbstractMessageConverter<Object> {
	public static final MimeType JSON_ALL = new MimeType("application", "*+json");

	public JsonMessageConverter() {
		supportMimeTypes.add(MimeTypeUtils.APPLICATION_JSON, JSON_ALL, TEXT_ALL);
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage) throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		if (text == null) {
			return null;
		}
		return getJsonSupport().parseObject(text, type.getType());
	}

	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String text = toJsonString(body, getJsonSupport());
		if (text == null) {
			return;
		}
		writeTextBody(text, contentType, outputMessage);
	}

	public static String toJsonString(Object body, JSONSupport jsonSupport) {
		if (body == null) {
			return null;
		}

		if (body instanceof ToMap) {
			return jsonSupport.toJSONString(MapperUtils.toMap(body));
		} else {
			return jsonSupport.toJSONString(body);
		}
	}
}