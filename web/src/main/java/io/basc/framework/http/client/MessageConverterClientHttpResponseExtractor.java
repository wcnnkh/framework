package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.net.convert.MessageConverter;

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

		if (HttpStatus.OK.getCode() != response.getRawStatusCode()) {
			return null;
		}

		if (messageConverter == null || !messageConverter.isReadable(responseType, response.getContentType())) {
			throw new UnsupportedOperationException("not supported read responseType=" + responseType);
		}

		return (T) messageConverter.readFrom(responseType, response);
	}

}
