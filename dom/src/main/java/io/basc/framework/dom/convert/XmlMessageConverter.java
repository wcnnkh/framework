package io.basc.framework.dom.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.ConversionServiceAware;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.dom.DomUtils;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.message.convert.AbstractMessageConverter;
import io.basc.framework.net.message.convert.MessageConvertException;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.value.StringValue;
import io.basc.framework.value.Value;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Document;

public class XmlMessageConverter extends AbstractMessageConverter<Object> implements ConversionServiceAware {
	private ConversionService conversionService;

	public XmlMessageConverter() {
		supportMimeTypes.add(MediaType.APPLICATION_XML, MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_XHTML_XML,
				MediaType.APPLICATION_RSS_XML);
	}

	@Override
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
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
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String writeBody;
		if (ClassUtils.isPrimitiveOrWrapper(type.getType()) || String.class == type.getType()
				|| Value.class.isAssignableFrom(type.getType())) {
			writeBody = body.toString();
		} else if (body instanceof Map) {
			writeBody = DomUtils.getDomBuilder().toString((Map) body);
		} else {
			Map map = getJsonSupport().parseObject(getJsonSupport().toJSONString(body), Map.class);
			writeBody = DomUtils.getDomBuilder().toString(map);
		}
		writeTextBody(writeBody, contentType, outputMessage);
	}
}
