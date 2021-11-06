package io.basc.framework.rpc.http;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.HttpConnection;

import java.util.concurrent.Callable;

public class HttpConnectionCallable implements Callable<Object> {
	private final HttpConnection httpConnection;
	private final TypeDescriptor responseType;

	public HttpConnectionCallable(HttpConnection httpConnection,
			TypeDescriptor responseType) {
		this.httpConnection = httpConnection;
		this.responseType = responseType;
	}

	public Object call() throws Exception {
		TypeDescriptor typeDescriptor = responseType;
		if(responseType.getType() == HttpResponseEntity.class){
			ResolvableType resolvableType = typeDescriptor.getResolvableType().getGeneric(0);
			typeDescriptor = typeDescriptor.convert(resolvableType);
		}
		
		HttpResponseEntity<Object> responseEntity = httpConnection.execute(typeDescriptor);
		if(responseType.getType() == HttpResponseEntity.class){
			return responseEntity;
		}
		return responseEntity.getBody();
	}
}
