package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.http.HttpEntity;
import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.net.convert.MessageConverter;

public class MessageConverterClientHttpRequestCallback implements ClientHttpRequestCallback {
	private final MessageConverter messageConverter;
	private final HttpEntity<?> httpEntity;

	public MessageConverterClientHttpRequestCallback(MessageConverter messageConverter, HttpEntity<?> httpEntity) {
		this.messageConverter = messageConverter;
		this.httpEntity = httpEntity;
	}

	@Override
	public ClientHttpRequest callback(ClientHttpRequest clientRequest) throws IOException {
		if (httpEntity == null) {
			return null;
		}

		if (httpEntity.getHeaders() != null) {
			clientRequest.getHeaders().putAll(httpEntity.getHeaders());
		}

		if (httpEntity.hasBody() && clientRequest.getMethod().hasRequestBody()) {
			if (messageConverter == null
					|| !messageConverter.isWriteable(httpEntity.getTypeDescriptor(), httpEntity.getContentType())) {
				throw new UnsupportedException("not supported write " + httpEntity);
			}

			messageConverter.writeTo(Value.of(httpEntity.getBody(), httpEntity.getTypeDescriptor()),
					httpEntity.getContentType(), clientRequest);
		}
		return clientRequest;
	}

}
