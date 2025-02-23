package io.basc.framework.net.convert.support;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MediaType;
import io.basc.framework.net.OutputMessage;
import io.basc.framework.util.io.MimeType;

public class ByteArrayMessageConverter extends ObjectMessageConverter<byte[]> {

	public ByteArrayMessageConverter() {
		getMediaTypeRegistry().addAll(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL));
	}

	@Override
	protected byte[] read(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		return inputMessage.readAllBytes();
	}

	@Override
	protected long getContentLength(byte[] body, MimeType contentType) {
		return body.length;
	}

	@Override
	protected void write(TypeDescriptor typeDescriptor, byte[] body, MediaType contentType, OutputMessage outputMessage)
			throws IOException {
		outputMessage.getOutputStreamPipeline().optional().ifPresent((e) -> e.write(body));
	}
}
