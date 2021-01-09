package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Document;

import scw.core.utils.TypeUtils;
import scw.dom.DomUtils;
import scw.http.MediaType;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.value.StringValue;
import scw.value.Value;

public class XmlMessageConverter extends AbstractMessageConverter<Object> {

	public XmlMessageConverter() {
		supportMimeTypes.add(MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML,
				MediaType.APPLICATION_XHTML_XML, MediaType.APPLICATION_RSS_XML);
	}

	@Override
	public boolean support(Class<?> clazz) {
		if (Collection.class.isAssignableFrom(clazz)) {
			return false;
		}
		return true;
	}

	@Override
	protected Object readInternal(Type type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		if (TypeUtils.isPrimitiveOrWrapper(type) || String.class == type
				|| Value.class == type) {
			StringValue value = new StringValue(text);
			value.setJsonSupport(getJsonSupport());
			return value.getAsObject(type);
		}

		Document document = DomUtils.getDomBuilder().parse(text);
		Map<String, Object> map;
		String jsonText;
		try {
			map = DomUtils.toRecursionMap(document);
			jsonText = getJsonSupport().toJSONString(map);
			return getJsonSupport().parseObject(jsonText, type);
		} catch (Exception e) {
			throw new MessageConvertException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void writeInternal(Type type, Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		String writeBody;
		if (TypeUtils.isPrimitiveOrWrapper(body.getClass())
				|| String.class == body.getClass()
				|| Value.class.isAssignableFrom(body.getClass())) {
			writeBody = body.toString();
		} else if (body instanceof Map) {
			writeBody = DomUtils.getDomBuilder().toString((Map)body);
		} else {
			Map map = getJsonSupport().parseObject(
					getJsonSupport().toJSONString(body), Map.class);
			writeBody = DomUtils.getDomBuilder().toString(map);
		}
		writeTextBody(writeBody, contentType, outputMessage);
	}
}
