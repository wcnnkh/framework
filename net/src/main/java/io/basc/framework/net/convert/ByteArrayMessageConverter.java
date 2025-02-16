package io.basc.framework.net.convert;

import java.io.IOException;
import java.util.Arrays;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypeUtils;
import io.basc.framework.net.OutputMessage;

public class ByteArrayMessageConverter extends ObjectMessageConverter<byte[]> {

	public ByteArrayMessageConverter() {
		getMimeTypeRegistry().addAll(Arrays.asList(MimeTypeUtils.APPLICATION_OCTET_STREAM, MimeTypeUtils.ALL));
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
	protected void write(TypeDescriptor typeDescriptor, byte[] body, MimeType contentType, OutputMessage outputMessage)
			throws IOException {
		outputMessage.write(body);
	}
}
