package scw.net.message.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import scw.lang.NotSupportException;
import scw.net.MimeType;
import scw.net.message.InputMessage;
import scw.net.message.OutputMessage;

public abstract class AbstractMessageConverterChain implements MessageConverterChain {
	private MessageConverterChain chain;

	public AbstractMessageConverterChain(MessageConverterChain chain) {
		this.chain = chain;
	}

	public Object read(Type type, InputMessage inputMessage) throws IOException {
		MessageConverter converter = getNextReadMessageConveter(type, inputMessage);
		if (converter == null) {
			if (chain == null) {
				return notSupportRead(type, inputMessage);
			} else {
				return chain.read(type, inputMessage);
			}
		} else {
			return converter.read(type, inputMessage, this);
		}
	}

	protected abstract MessageConverter getNextReadMessageConveter(Type type, InputMessage inputMessage)
			throws IOException;

	protected Object notSupportRead(Type type, InputMessage inputMessage) throws IOException {
		throw new NotSupportException(type.toString());
	}

	public void write(Object body, MimeType contentType, OutputMessage outputMessage) throws IOException {
		MessageConverter converter = getNextWriteMessageConveter(body, contentType, outputMessage);
		if (converter == null) {
			if (chain == null) {
				notSupportWrite(body, contentType, outputMessage);
			} else {
				chain.write(body, contentType, outputMessage);
			}
		} else {
			converter.write(body, contentType, outputMessage, this);
		}
	}

	protected abstract MessageConverter getNextWriteMessageConveter(Object body, MimeType contentType,
			OutputMessage outputMessage) throws IOException;

	protected void notSupportWrite(Object body, MimeType contentType, OutputMessage outputMessage) throws IOException {
		throw new NotSupportException(body + " --> " + contentType);
	}
}
