package io.basc.framework.xml.convert;

import java.io.IOException;
import java.util.Collection;

import org.w3c.dom.Document;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.config.ConversionService;
import io.basc.framework.core.convert.config.ConversionServiceAware;
import io.basc.framework.http.MediaType;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.AbstractMessageConverter;
import io.basc.framework.net.convert.MessageConvertException;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.xml.XmlUtils;

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
	public boolean isSupported(Class<?> clazz) {
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
			return Value.of(text).getAsObject(type);
		}

		Document document = XmlUtils.getTemplate().getParser().parse(text);
		return conversionService.convert(document, TypeDescriptor.valueOf(Document.class), type);
	}

	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		String writeBody;
		if (ClassUtils.isPrimitiveOrWrapper(type.getType()) || String.class == type.getType()) {
			writeBody = body.toString();
		} else {
			Document document = XmlUtils.getTemplate().parse(body, type);
			writeBody = XmlUtils.getTemplate().toString(document);
		}
		writeTextBody(writeBody, contentType, outputMessage);
	}
}
