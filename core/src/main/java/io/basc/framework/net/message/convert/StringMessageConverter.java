package io.basc.framework.net.message.convert;

import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.IOUtils;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;

import java.io.IOException;

public class StringMessageConverter extends AbstractMessageConverter<Object> {
	private final ConversionService conversionService;

	public StringMessageConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
		supportMimeTypes.add(MimeTypeUtils.TEXT_PLAIN, TEXT_ALL);
		setSupportBytes(true);
	}

	@Override
	public boolean isSupported(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		String text = readTextBody(inputMessage);
		if (type.getResolvableType().getRawClass() == byte[].class) {
			return text.getBytes(getCharset(inputMessage));
		}

		return conversionService.convert(text, TypeDescriptor.valueOf(String.class), type);
	}

	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		if (body instanceof byte[]) {
			IOUtils.write((byte[]) body, outputMessage.getOutputStream());
		} else {
			writeTextBody(body.toString(), contentType, outputMessage);
		}
	}
}
