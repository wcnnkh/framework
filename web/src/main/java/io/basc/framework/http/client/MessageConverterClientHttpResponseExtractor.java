package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.core.convert.TargetDescriptor;
import io.basc.framework.http.HttpRequest;
import io.basc.framework.http.HttpStatus;
import io.basc.framework.net.convert.MessageConverter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageConverterClientHttpResponseExtractor<T> implements ClientHttpResponseExtractor<T> {
	@NonNull
	private final MessageConverter messageConverter;
	@NonNull
	private final TargetDescriptor targetDescriptor;

	@SuppressWarnings("unchecked")
	@Override
	public T execute(HttpRequest request, ClientHttpResponse response) throws IOException {
		if (!request.getMethod().hasResponseBody()) {
			return null;
		}

		if (HttpStatus.OK.getCode() != response.getRawStatusCode()) {
			return null;
		}

		return (T) messageConverter.readFrom(targetDescriptor, response.getContentType(), response, response);
	}

}
