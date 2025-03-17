package run.soeasy.framework.http.client;

import java.io.IOException;

import run.soeasy.framework.http.HttpEntity;
import run.soeasy.framework.net.convert.MessageConverter;

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
					|| !messageConverter.isWriteable(httpEntity.getBody(), httpEntity.getContentType())) {
				throw new UnsupportedOperationException("not supported write " + httpEntity);
			}

			messageConverter.writeTo(httpEntity.getBody().any(), httpEntity.getContentType(), clientRequest,
					clientRequest);
		}
		return clientRequest;
	}

}
