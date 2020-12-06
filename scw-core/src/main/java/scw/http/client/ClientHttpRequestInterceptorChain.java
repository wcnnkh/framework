package scw.http.client;

import java.io.IOException;
import java.util.Iterator;

public class ClientHttpRequestInterceptorChain {
	private Iterator<ClientHttpRequestInterceptor> iterator;
	private ClientHttpRequestInterceptorChain nextChain;
	
	public ClientHttpRequestInterceptorChain(Iterator<ClientHttpRequestInterceptor> iterator){
		this(iterator, null);
	}
	
	public ClientHttpRequestInterceptorChain(Iterator<ClientHttpRequestInterceptor> iterator, ClientHttpRequestInterceptorChain nextChain){
		this.iterator = iterator;
		this.nextChain = nextChain;
	}
	
	public ClientHttpResponse intercept(ClientHttpRequest request) throws IOException{
		ClientHttpRequestInterceptor interceptor = getNextInterceptor(request);
		if(interceptor == null){
			if(nextChain == null){
				return request.execute();
			}else{
				return nextChain.intercept(request);
			}
		}
		return interceptor.intercept(request, this);
	}
	
	protected ClientHttpRequestInterceptor getNextInterceptor(ClientHttpRequest request){
		if(iterator == null){
			return null;
		}
		
		while(iterator.hasNext()){
			ClientHttpRequestInterceptor interceptor = iterator.next();
			if(interceptor != null){
				return interceptor;
			}
		}
		return null;
	}
}
