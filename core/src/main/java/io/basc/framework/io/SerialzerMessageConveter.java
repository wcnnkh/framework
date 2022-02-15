package io.basc.framework.io;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;
import io.basc.framework.net.message.convert.AbstractMessageConverter;
import io.basc.framework.net.message.convert.MessageConvertException;

import java.io.IOException;

public class SerialzerMessageConveter extends AbstractMessageConverter<Object> {
	private Serializer serializer;

	public SerialzerMessageConveter(Serializer serializer) {
		supportMimeTypes.add(MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL);
		this.serializer = serializer;
	}

	@Override
	public boolean support(Class<?> clazz) {
		return true;
	}

	@Override
	protected Object readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		try {
			return serializer.deserialize(inputMessage.getInputStream());
		} catch (ClassNotFoundException e) {
			throw new MessageConvertException(e);
		}
	}

	@Override
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException {
		serializer.serialize(body, outputMessage.getOutputStream());
	}

}
