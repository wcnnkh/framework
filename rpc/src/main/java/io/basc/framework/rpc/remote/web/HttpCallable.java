package io.basc.framework.rpc.remote.web;

import io.basc.framework.http.HttpHeaders;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.client.ClientHttpRequest;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.ClientHttpResponse;
import io.basc.framework.http.client.exception.HttpStatusCodeException;
import io.basc.framework.rpc.RemoteException;
import io.basc.framework.rpc.remote.RemoteMessageCodec;
import io.basc.framework.rpc.remote.RemoteRequestMessage;
import io.basc.framework.rpc.remote.RemoteResponseMessage;

import java.net.URI;
import java.util.concurrent.Callable;

public class HttpCallable implements Callable<Object> {
	private final ClientHttpRequestFactory requestFactory;
	private final RemoteMessageCodec messageCodec;
	private final RemoteRequestMessage requestMessage;
	private final URI uri;
	private final HttpHeaders httpHeaders;

	public HttpCallable(ClientHttpRequestFactory requestFactory,
			RemoteMessageCodec messageCodec, RemoteRequestMessage requestMessage, URI uri, HttpHeaders httpHeaders) {
		this.messageCodec = messageCodec;
		this.requestFactory = requestFactory;
		this.requestMessage = requestMessage;
		this.uri = uri;
		this.httpHeaders = httpHeaders;
	}

	public Object call() throws Exception {
		ClientHttpRequest httpRequest = requestFactory.createRequest(uri, HttpMethod.POST);
		if(httpHeaders != null){
			httpRequest.getHeaders().putAll(httpHeaders);
		}
		
		messageCodec.encode(httpRequest, requestMessage);
		ClientHttpResponse response = null;
		RemoteResponseMessage responseMessage;
		try {
			response = httpRequest.execute();
			if(response.getStatusCode().isError()){
				throw new HttpStatusCodeException(response.getStatusCode());
			}
			
			responseMessage = messageCodec.decode(response, requestMessage);
		} finally{
			if(response != null){
				response.close();
			}
		}
		if (responseMessage.getThrowable() == null) {
			return responseMessage.getBody();
		}
		throw new RemoteException(uri.toString(),
				responseMessage.getThrowable());
	}

}
