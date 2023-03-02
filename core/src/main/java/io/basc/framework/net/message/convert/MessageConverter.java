package io.basc.framework.net.message.convert;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.MimeTypes;
import io.basc.framework.net.message.InputMessage;
import io.basc.framework.net.message.OutputMessage;

import java.io.IOException;

public interface MessageConverter {
	/**
	 * 返回只读的MimeTypes,
	 * 如果返回{@link MimeTypes#EMPTY} 或
	 * {@code null}说明可以接受任意类型，具体看{@link #canRead(TypeDescriptor, MimeType)} or
	 * {@link #canWrite(TypeDescriptor, Object, MimeType)}
	 * 
	 * @return
	 */
	MimeTypes getSupportMimeTypes();

	boolean canRead(TypeDescriptor typeDescriptor, MimeType mimeType);

	boolean canWrite(TypeDescriptor typeDescriptor, Object body, MimeType contentType);

	Object read(TypeDescriptor typeDescriptor, InputMessage inputMessage) throws IOException, MessageConvertException;

	void write(TypeDescriptor typeDescriptor, Object body, MimeType contentType, OutputMessage outputMessage)
			throws IOException, MessageConvertException;
}
