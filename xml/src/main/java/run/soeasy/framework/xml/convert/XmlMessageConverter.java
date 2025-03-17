package run.soeasy.framework.xml.convert;

import java.io.IOException;
import java.util.Collection;

import org.w3c.dom.Document;

import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.http.MediaType;
import run.soeasy.framework.net.InputMessage;
import run.soeasy.framework.net.MimeType;
import run.soeasy.framework.net.OutputMessage;
import run.soeasy.framework.net.convert.AbstractMessageConverter;
import run.soeasy.framework.net.convert.MessageConvertException;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.xml.XmlUtils;

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
				|| Source.class == type.getType()) {
			return Source.of(text).getAsObject(type);
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
