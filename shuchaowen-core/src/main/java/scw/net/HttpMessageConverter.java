package scw.net;

import java.lang.reflect.Type;

public abstract class HttpMessageConverter implements MessageConverter {

	public Object convert(Message message, Type type, MessageConverterChain chain) throws Throwable {
		if (message instanceof HttpMessage) {
			return convert((HttpMessage) message, type, chain);
		}
		return chain.doConvert(message, type);
	}

	protected abstract Object convert(HttpMessage message, Type type, MessageConverterChain chain) throws Throwable;
}
