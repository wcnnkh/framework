package io.basc.framework.rpc.remote.web;

import java.net.URI;
import java.util.concurrent.Callable;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.HttpClient;
import io.basc.framework.http.client.exception.HttpStatusCodeException;
import io.basc.framework.rpc.RemoteException;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.RemoteRequestMessage;
import io.basc.framework.rpc.remote.RemoteResponseMessage;

public class HttpCallable implements Callable<Object> {
	private final HttpClient httpClient;
	private final RemoteMessageCodec messageCodec;
	private final RemoteRequestMessage requestMessage;
	private final URI uri;
	private final HttpHeaders httpHeaders;

	public HttpCallable(HttpClient httpClient, RemoteMessageCodec messageCodec, RemoteRequestMessage requestMessage,
			URI uri, HttpHeaders httpHeaders) {
		this.messageCodec = messageCodec;
		this.httpClient = httpClient;
		this.requestMessage = requestMessage;
		this.uri = uri;
		this.httpHeaders = httpHeaders;
	}

	public Object call() throws Exception {
		HttpResponseEntity<RemoteResponseMessage> responseEntity = httpClient.execute(uri, HttpMethod.POST.name(),
				(request) -> {
					if (httpHeaders != null) {
						request.getHeaders().putAll(httpHeaders);
					}

					messageCodec.encode(request, requestMessage);
					return request;
				}, (request, response) -> {
					try {
						if (response.getStatusCode().isError()) {
							throw new HttpStatusCodeException(response.getStatusCode());
						}

						return messageCodec.decode(response, requestMessage);
					} finally {
						if (response != null) {
							response.close();
						}
					}
				});

		RemoteResponseMessage responseMessage = responseEntity.getBody();
		if (responseMessage.getThrowable() == null) {
			return responseMessage.getBody();
		}
		throw new RemoteException(uri.toString(), responseMessage.getThrowable());
	}

}
