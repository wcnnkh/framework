package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;
import scw.net.mime.MimeType;

public interface MessageConverterChain {
	Object read(Type type, InputMessage inputMessage) throws IOException;

	void write(Object body, MimeType contentType, OutputMessage outputMessage) throws IOException;
}
