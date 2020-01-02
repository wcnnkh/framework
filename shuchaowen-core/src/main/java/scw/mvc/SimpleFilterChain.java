package scw.mvc;

import java.util.Collection;

public class SimpleFilterChain extends AbstractFilterChain {
	private final FilterChain chain;

	public SimpleFilterChain(Collection<Filter> filters) {
		this(filters, null);
	}

	public SimpleFilterChain(Collection<Filter> filters, FilterChain chain) {
		super(filters);
		this.chain = chain;
	}

	@Override
	protected Object lastFilter(Channel channel) throws Throwable {
		return chain == null ? null : chain.doFilter(channel);
	}
}
