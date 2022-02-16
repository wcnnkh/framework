package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.net.message.convert.MessageConverter;

public class MessageConverterClientHttpResponseExtractor<T> implements ClientHttpResponseExtractor<T> {
	private final MessageConverter messageConverter;
	private final TypeDescriptor responseType;

	public MessageConverterClientHttpResponseExtractor(MessageConverter messageConverter, TypeDescriptor responseType) {
		this.messageConverter = messageConverter;
		this.responseType = responseType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T execute(HttpRequest request, ClientHttpResponse response) throws IOException {
		if (!request.getMethod().hasResponseBody()) {
			return null;
		}

		if (HttpStatus.OK.value() != response.getRawStatusCode()) {
			return null;
		}

		if (messageConverter == null || !messageConverter.canRead(responseType, response.getContentType())) {
			throw new NotSupportedException("not supported read responseType=" + responseType);
		}

		return (T) messageConverter.read(responseType, response);
	}

}
