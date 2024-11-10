package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.io.Serializer;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.OutputMessage;

public class SerialzerMessageConveter extends AbstractMessageConverter {
	private Serializer serializer;

	public SerialzerMessageConveter(Serializer serializer) {
		getMimeTypes().add(MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL);
		this.serializer = serializer;
	}

	@Override
	protected Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		try {
			return serializer.deserialize(inputMessage.getInputStream());
		} catch (ClassNotFoundException e) {
			throw new MessageConvertException(e);
		}
	}

	@Override
	protected void doWrite(ValueWrapper source, MimeType contentType, OutputMessage outputMessage) throws IOException {
		serializer.serialize(source.getValue(), outputMessage.getOutputStream());
	}

}
