package scw.mvc.action.filter;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public class DefaultFilterChain implements FilterChain{
	private Collection<? extends Filter> filters;
	private FilterChain chain;
	
	public DefaultFilterChain(Collection<? extends Filter> filters, FilterChain chain){
		this.filters = filters;
		this.chain = chain;
	}
	
	public Object doFilter(Channel channel, Action action) throws Throwable {
		FilterChain filterChain = new IteratorFilterChain(filters, chain);
		return filterChain.doFilter(channel, action);
	}

}
