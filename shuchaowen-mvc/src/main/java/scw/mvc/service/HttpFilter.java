package scw.mvc.service;

import scw.mvc.Channel;
import scw.mvc.http.HttpChannel;

public abstract class HttpFilter implements Filter{

	public final Object doFilter(Channel channel, FilterChain chain)
			throws Throwable {
		if(channel instanceof HttpChannel){
			return doHttpFilter((HttpChannel)channel, chain);
		}else{
			return notHttpFilter(channel, chain);
		}
	}
	
	protected Object notHttpFilter(Channel channel, FilterChain chain) throws Throwable{
		return chain.doFilter(channel);
	}
	
	protected abstract Object doHttpFilter(HttpChannel channel, FilterChain chain) throws Throwable;
}
