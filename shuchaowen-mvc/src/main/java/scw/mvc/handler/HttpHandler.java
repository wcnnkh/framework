package scw.mvc.handler;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpHandler implements Handler{

	public final void doHandler(Channel channel, HandlerChain chain)
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
