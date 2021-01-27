package scw.net.message.converter;

import java.io.IOException;

import scw.core.ResolvableType;
import scw.net.MimeType;
import scw.net.MimeTypes;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public interface MessageConverter{
	/**
	 * 返回只读的MimeTypes
	 * @return
	 */
	MimeTypes getSupportMimeTypes();
	
	boolean canRead(ResolvableType type, MimeType mimeType);
	
	boolean canWrite(ResolvableType type, Object body, MimeType contentType);
	
	boolean canWrite(Object body, MimeType contentType);
	
	Object read(ResolvableType type, InputMessage inputMessage) throws IOException,
			MessageConvertException;
	
	void write(ResolvableType type, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
	
	void write(Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
}
