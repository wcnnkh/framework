package scw.net.message.convert;

import java.io.IOException;

import scw.convert.TypeDescriptor;
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
	
	boolean canRead(TypeDescriptor typeDescriptor, MimeType mimeType);
	
	boolean canWrite(TypeDescriptor typeDescriptor, Object body, MimeType contentType);
	
	boolean canWrite(Object body, MimeType contentType);
	
	Object read(TypeDescriptor typeDescriptor, InputMessage inputMessage) throws IOException,
			MessageConvertException;
	
	void write(TypeDescriptor typeDescriptor, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
	
	void write(Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
}
