package scw.mvc.service;

import java.util.Collection;

import scw.mvc.Channel;

public class DefaultFilterChain implements FilterChain {
	private final Collection<? extends Filter> handlers;
	private final FilterChain chain;

	public DefaultFilterChain(Collection<? extends Filter> handlers,
			FilterChain chain) {
		this.handlers = handlers;
		this.chain = chain;
	}

	public Object doFilter(Channel channel) throws Throwable {
		FilterChain chain = new IteratorFilterChain(handlers, this.chain);
		return chain.doFilter(channel);
	}

}
