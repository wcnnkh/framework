package io.basc.framework.net.convert;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.io.IOUtils;
import io.basc.framework.net.InputMessage;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.OutputMessage;

public class ByteArrayMessageConverter extends ObjectMessageConverter<byte[]> {

	public ByteArrayMessageConverter() {
		getMimeTypes().add(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
	}

	@Override
	protected byte[] read(TypeDescriptor typeDescriptor, MimeType contentType, InputMessage inputMessage)
			throws IOException {
		return IOUtils.toByteArray(inputMessage.getInputStream());
	}

	@Override
	protected long getContentLength(byte[] body, MimeType contentType) {
		return body.length;
	}

	@Override
	protected void write(TypeDescriptor typeDescriptor, byte[] body, MimeType contentType, OutputMessage outputMessage)
			throws IOException {
		outputMessage.getOutputStream().write(body);
	}
}
