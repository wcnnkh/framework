package io.basc.framework.net.convert;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.serializer.Serializer;

public class SerialzerMessageConveter extends AbstractMessageConverter {
	private final Serializer serializer;

	public SerialzerMessageConveter(Serializer serializer) {
		getMimeTypeRegistry().addAll(Arrays.asList(MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL));
		this.serializer = serializer;
	}

	@Override
	protected Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		return inputMessage.getInputStream().export().map(source -> {
			try {
				return serializer.deserialize(source);
			} catch (ClassNotFoundException e) {
				throw new MessageConvertException(e);
			}
		}).get();
	}

	@Override
	protected void doWrite(Value source, MimeType contentType, OutputMessage outputMessage) throws IOException {
		outputMessage.getOutputStream().export().ifPresent((output) -> {
			byte[] data = serializer.serialize(source.get());
			output.write(data);
		});
	}

}
