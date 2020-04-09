package scw.mvc.handler;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpHandler implements Handler{

	public final Object doHandler(Channel channel, HandlerChain chain)
			throws Throwable {
		if(channel instanceof HttpChannel){
			return doHttpHandler((HttpChannel)channel, chain);
		}else{
			return notHttpHandler(channel, chain);
		}
	}
	
	protected Object notHttpHandler(Channel channel, HandlerChain chain) throws Throwable{
		return chain.doHandler(channel);
	}
	
	protected abstract Object doHttpHandler(HttpChannel channel, HandlerChain chain) throws Throwable;
}
