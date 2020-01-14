package scw.net.message.converter;

import java.lang.reflect.Type;

import scw.net.message.HttpInputMessage;
import scw.net.message.InputMessage;

public abstract class HttpInputMessageConverter implements MessageConverter {

	public Object convert(InputMessage inputMessage, Type type, MessageConverterChain chain) throws Throwable {
		if (inputMessage instanceof HttpInputMessage) {
			return convert((HttpInputMessage) inputMessage, type, chain);
		}
		return chain.doConvert(inputMessage, type);
	}

	protected abstract Object convert(HttpInputMessage message, Type type, MessageConverterChain chain) throws Throwable;
}
