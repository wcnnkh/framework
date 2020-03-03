package scw.mvc.http;

import scw.mvc.Channel;
import scw.mvc.handler.Handler;
import scw.mvc.handler.HandlerChain;

public abstract class HttpHandler implements Handler{

	public void doHandler(Channel channel, HandlerChain chain)
			throws Throwable {
		if(channel instanceof HttpChannel){
			doHttpHandler((HttpChannel)channel, chain);
		}else{
			notHttpHandler(channel, chain);
		}
	}
	
	protected void notHttpHandler(Channel channel, HandlerChain chain) throws Throwable{
		chain.doHandler(channel);
	}
	
	protected abstract void doHttpHandler(HttpChannel channel, HandlerChain chain) throws Throwable;
}
