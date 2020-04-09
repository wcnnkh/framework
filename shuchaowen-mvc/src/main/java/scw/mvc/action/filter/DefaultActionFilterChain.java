package scw.mvc.action.filter;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public class DefaultActionFilterChain implements ActionFilterChain{
	private Collection<? extends ActionFilter> filters;
	private ActionFilterChain chain;
	
	public DefaultActionFilterChain(Collection<? extends ActionFilter> filters, ActionFilterChain chain){
		this.filters = filters;
		this.chain = chain;
	}
	
	public Object doFilter(Channel channel, Action action) throws Throwable {
		ActionFilterChain filterChain = new IteratorActionFilterChain(filters, chain);
		return filterChain.doFilter(channel, action);
	}

}
