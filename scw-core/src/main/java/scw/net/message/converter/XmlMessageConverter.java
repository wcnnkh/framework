package scw.net.message.converter;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Document;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.core.utils.ClassUtils;
import scw.dom.DomUtils;
import scw.http.MediaType;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.value.StringValue;
import scw.value.Value;

public class XmlMessageConverter extends AbstractMessageConverter<Object> {
	private final ConversionService conversionService;
	
	public XmlMessageConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
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
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		if (ClassUtils.isPrimitiveOrWrapper(type.getType()) || String.class == type.getType()
				|| Value.class == type.getType()) {
			StringValue value = new StringValue(text);
			value.setJsonSupport(getJsonSupport());
			return value.getAsObject(type.getResolvableType());
		}

		Document document = DomUtils.getDomBuilder().parse(text);
		return conversionService.convert(document, TypeDescriptor.valueOf(Document.class), type);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		String writeBody;
		if (ClassUtils.isPrimitiveOrWrapper(type.getType())
				|| String.class == type.getType()
				|| Value.class.isAssignableFrom(type.getType())) {
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
