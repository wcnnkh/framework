package io.basc.framework.feign;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.FastByteArrayOutputStream;
import io.basc.framework.net.MimeType;
import io.basc.framework.net.message.convert.MessageConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;

public class FeignEncoder implements Encoder {
	private MessageConverter messageConverter;

	public FeignEncoder(MessageConverter messageConverter) {
		this.messageConverter = messageConverter;
	}

	public void encode(Object body, Type bodyType, RequestTemplate template) throws EncodeException {
		FastByteArrayOutputStream byteBody = new FastByteArrayOutputStream();
		FeignOutputMessage outputMessage = new FeignOutputMessage(template, byteBody);
		TypeDescriptor typeDescriptor = TypeDescriptor.forObject(body);
		if (messageConverter.canWrite(typeDescriptor, body, outputMessage.getContentType())) {
			try {
				messageConverter.write(typeDescriptor, body, outputMessage.getContentType(), outputMessage);

				MimeType mimeType = outputMessage.getContentType();
				Charset charset = mimeType.getCharset();
				if (charset == null) {
					charset = template.requestCharset();
				}

				template.body(byteBody.toByteArray(), charset);
				Map<String, Collection<String>> headers = new HashMap<String, Collection<String>>();
				for (Entry<String, List<String>> entry : outputMessage.getHeaders().entrySet()) {
					headers.put(entry.getKey(), entry.getValue());
				}
				template.headers(headers);
			} catch (IOException e) {
				throw new EncodeException("bodyType=" + bodyType, e);
			}
		}
		throw new EncodeException("not support type:" + bodyType + ", body=" + body);
	}
}
