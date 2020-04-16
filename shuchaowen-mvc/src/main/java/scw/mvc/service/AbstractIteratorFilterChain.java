package scw.mvc.service;

import scw.mvc.Channel;

public abstract class AbstractIteratorFilterChain implements FilterChain {
	private final FilterChain chain;

	public AbstractIteratorFilterChain(FilterChain chain) {
		this.chain = chain;
	}

	public final Object doFilter(Channel channel) throws Throwable {
		Filter channelHandler = getNextFilter(channel);
		if (channelHandler == null) {
			if (chain == null) {
				return null;
			} else {
				return chain.doFilter(channel);
			}
		} else {
			return channelHandler.doFilter(channel, this);
		}
	}

	protected abstract Filter getNextFilter(Channel channel) throws Throwable;
}
