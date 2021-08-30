package io.basc.framework.net.message.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.MediaType;
import io.basc.framework.io.IOUtils;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;

import java.io.IOException;

public class ByteArrayMessageConverter extends AbstractMessageConverter<byte[]>{
	
	public ByteArrayMessageConverter(){
		supportMimeTypes.add(MediaType.APPLICATION_OCTET_STREAM, MediaType.ALL);
		setSupportBytes(true);
	}
	
	@Override
	public boolean support(Class<?> clazz) {
		return clazz == byte[].class;
	}

	@Override
	protected byte[] readInternal(TypeDescriptor type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		return IOUtils.toByteArray(inputMessage.getInputStream());
	}

	@Override
	protected void writeInternal(TypeDescriptor type, byte[] body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		outputMessage.getOutputStream().write(body);
	}
	
	@Override
	protected Long getContentLength(byte[] body, MimeType contentType)
			throws IOException {
		return (long) body.length;
	}
}
