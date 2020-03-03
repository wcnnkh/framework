package scw.mvc.action.http;

import scw.lang.NotSupportException;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.Filter;
import scw.mvc.action.filter.FilterChain;
import scw.mvc.http.HttpChannel;

public abstract class HttpFilter implements Filter{

	public final Object doFilter(Channel channel, Action action, FilterChain chain)
			throws Throwable {
		if(channel instanceof HttpChannel && action instanceof HttpAction){
			return doHttpFilter((HttpChannel)channel, (HttpAction)action, chain);
		}else{
			return doNoHttpFilter(channel, action, chain);
		}
	}
	
	protected Object doNoHttpFilter(Channel channel, Action action, FilterChain chain) throws Throwable{
		throw new NotSupportException(channel.toString());
	}

	protected abstract Object doHttpFilter(HttpChannel channel, HttpAction action, FilterChain chain) throws Throwable;
}
