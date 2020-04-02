package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Enumeration;

import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.message.converter.MessageConvertException;

public interface MessageConverter{
	Enumeration<MimeType> enumerationSupportMimeTypes();
	
	boolean canRead(Type type, MimeType mimeType);

	boolean canWrite(Object body, MimeType contentType);
	
	Object read(Type type, InputMessage inputMessage) throws IOException,
			MessageConvertException;

	void write(Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
}
