package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

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
	
	boolean canRead(Type type, MimeType mimeType);

	boolean canWrite(Object body, MimeType contentType);
	
	Object read(Type type, InputMessage inputMessage) throws IOException,
			MessageConvertException;

	void write(Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
}
