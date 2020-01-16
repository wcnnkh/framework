package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public interface MessageConverter {
	Object read(Type type, InputMessage inputMessage, MessageConverterChain chain)
			throws IOException, MessageConvertException;

	void write(Object body, MimeType contentType, OutputMessage outputMessage, MessageConverterChain chain)
			throws IOException, MessageConvertException;
}
