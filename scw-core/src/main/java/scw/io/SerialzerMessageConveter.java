package scw.io;

import java.io.IOException;

import scw.convert.TypeDescriptor;
import scw.net.MimeType;
import scw.net.MimeTypeUtils;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.convert.AbstractMessageConverter;
import scw.net.message.convert.MessageConvertException;

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
	protected void writeInternal(TypeDescriptor type, Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		serializer.serialize(outputMessage.getOutputStream(), body);
	}

}
