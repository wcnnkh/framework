package scw.net.message.converter;

import java.lang.reflect.Type;

import scw.net.message.InputMessage;

public interface MessageConverter {
	Object convert(InputMessage inputMessage, Type type, MessageConverterChain chain) throws Throwable;
}
