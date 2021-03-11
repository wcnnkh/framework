package scw.net.message.converter;

import java.io.IOException;

import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.io.IOUtils;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public class StringMessageConverter extends AbstractMessageConverter<Object> {
	private final ConversionService conversionService;
	
	public StringMessageConverter(ConversionService conversionService) {
		this.conversionService = conversionService;
		supportMimeTypes.add(MimeTypeUtils.TEXT_PLAIN, TEXT_ALL);
		setSupportBytes(true);
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage) throws IOException, MessageConvertException {
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
			IOUtils.write((byte[]) body, outputMessage.getBody());
		} else {
			writeTextBody(body.toString(), contentType, outputMessage);
		}
	}
}
