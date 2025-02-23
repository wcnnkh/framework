package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.net.convert.MessageConvertException;
import io.basc.framework.util.io.MimeType;
import io.basc.framework.util.io.serializer.Serializer;

public class SerialzerMessageConveter extends AbstractMessageConverter {
	private final Serializer serializer;

	public SerialzerMessageConveter(Serializer serializer) {
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
		this.serializer = serializer;
	}

	@Override
	protected Object doRead(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		return inputMessage.getInputStreamPipeline().optional().map(source -> {
			try {
				return serializer.deserialize(source);
			} catch (ClassNotFoundException e) {
				throw new MessageConvertException(e);
			}
		}).get();
	}

	@Override
	protected void doWrite(Source source, MediaType contentType, OutputMessage outputMessage) throws IOException {
		outputMessage.getOutputStreamPipeline().optional().ifPresent((output) -> {
			byte[] data = serializer.serialize(source.get());
			output.write(data);
		});
	}

}
