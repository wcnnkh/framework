package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.io.IOUtils;
import scw.net.MimeType;
import scw.net.http.MediaType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.converter.MessageConvertException;

public class ByteArrayMessageConverter extends AbstractMessageConverter<byte[]>{
	private static final long serialVersionUID = 1L;
	
	public ByteArrayMessageConverter(){
		add(MediaType.APPLICATION_OCTET_STREAM);
		add(MediaType.ALL);
	}
	
	@Override
	public boolean support(Class<?> clazz) {
		return clazz == byte[].class;
	}

	@Override
	protected byte[] readInternal(Type type, InputMessage inputMessage)
			throws IOException, MessageConvertException {
		return IOUtils.toByteArray(inputMessage.getBody());
	}

	@Override
	protected void writeInternal(byte[] body, MimeType contentType,
			OutputMessage outputMessage) throws IOException,
			MessageConvertException {
		outputMessage.getBody().write(body);
	}
	
	@Override
	protected Long getContentLength(byte[] body, MimeType contentType)
			throws IOException {
		return (long) body.length;
	}
}
