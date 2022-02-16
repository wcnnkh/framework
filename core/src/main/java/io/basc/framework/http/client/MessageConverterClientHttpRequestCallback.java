package io.basc.framework.http.client;

import java.io.IOException;

import io.basc.framework.http.HttpEntity;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.lang.NotSupportedException;
import io.basc.framework.net.message.convert.MessageConverter;

public class MessageConverterClientHttpRequestCallback implements ClientHttpRequestCallback {
	private final MessageConverter messageConverter;
	private final HttpEntity<?> httpEntity;

	public MessageConverterClientHttpRequestCallback(MessageConverter messageConverter, HttpEntity<?> httpEntity) {
		this.messageConverter = messageConverter;
		this.httpEntity = httpEntity;
	}

	@Override
	public ClientHttpRequest callback(ClientHttpRequest clientRequest) throws IOException {
		final boolean needWriteBody = httpEntity != null && httpEntity.hasBody()
				&& clientRequest.getMethod() != HttpMethod.GET;
		if (clientRequest.getMethod().hasRequestBody()) {
			if (messageConverter == null || !messageConverter.canWrite(httpEntity.getTypeDescriptor(),
					httpEntity.getBody(), httpEntity == null ? null : httpEntity.getContentType())) {
				throw new NotSupportedException("not supported write " + httpEntity);
			}
		}

		if (httpEntity.getHeaders() != null) {
			clientRequest.getHeaders().putAll(httpEntity.getHeaders());
		}

		if (needWriteBody) {
			messageConverter.write(httpEntity.getTypeDescriptor(), httpEntity.getBody(),
					httpEntity == null ? null : httpEntity.getContentType(), clientRequest);
		}
		return clientRequest;
	}

}
