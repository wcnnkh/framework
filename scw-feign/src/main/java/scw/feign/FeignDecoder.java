package scw.feign;

import java.io.IOException;
import java.lang.reflect.Type;

import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.codec.Decoder;
import scw.convert.TypeDescriptor;
import scw.net.message.converter.MessageConverter;

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
