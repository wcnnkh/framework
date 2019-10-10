package scw.net;

import java.lang.reflect.Type;

public interface MessageConverter {
	Object convert(Message message, Type type, MessageConverterChain chain) throws Throwable;
}
