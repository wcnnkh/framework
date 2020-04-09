package scw.mvc.action.http;

import scw.lang.NotSupportException;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.mvc.action.filter.ActionFilter;
import scw.mvc.action.filter.ActionFilterChain;
import scw.mvc.http.HttpChannel;

public abstract class HttpActionFilter implements ActionFilter{

	public final Object doFilter(Channel channel, Action action, ActionFilterChain chain)
			throws Throwable {
		if(channel instanceof HttpChannel && action instanceof HttpAction){
			return doHttpFilter((HttpChannel)channel, (HttpAction)action, chain);
		}else{
			return doNoHttpFilter(channel, action, chain);
		}
	}
	
	protected Object doNoHttpFilter(Channel channel, Action action, ActionFilterChain chain) throws Throwable{
		throw new NotSupportException(channel.toString());
	}

	protected abstract Object doHttpFilter(HttpChannel channel, HttpAction action, ActionFilterChain chain) throws Throwable;
}
