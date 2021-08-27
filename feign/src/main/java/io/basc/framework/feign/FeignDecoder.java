package io.basc.framework.feign;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.net.message.convert.MessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;

public class FeignDecoder implements Decoder {
	private MessageConverter messageConverter;

	public FeignDecoder(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
		FeignInputMessage inputMessage = new FeignInputMessage(response);
		return messageConverter.read(TypeDescriptor.valueOf(type), inputMessage);
	}
}
