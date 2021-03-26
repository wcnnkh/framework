package scw.rpc.http;

import java.util.concurrent.Callable;

import scw.convert.TypeDescriptor;
import scw.core.ResolvableType;
import scw.http.HttpResponseEntity;
import scw.http.client.HttpConnection;

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
			typeDescriptor = new TypeDescriptor(resolvableType, resolvableType.getRawClass(), typeDescriptor.getAnnotations());
		}
		
		HttpResponseEntity<Object> responseEntity = httpConnection.execute(typeDescriptor);
		if(responseType.getType() == HttpResponseEntity.class){
			return responseEntity;
		}
		return responseEntity.getBody();
	}
}
