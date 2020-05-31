package scw.aop;

import java.util.Collection;
import java.util.Collections;

public final class DefaultFilterChain implements FilterChain{
	private final FilterChain filterChain;
	private final Collection<? extends Filter> filters;
	
	public DefaultFilterChain(Collection<? extends Filter> filters) {
		this(filters, null);
	}
	
	@SuppressWarnings("unchecked")
	public DefaultFilterChain(Collection<? extends Filter> filters, FilterChain filterChain){
		this.filters = filters == null? Collections.EMPTY_LIST:filters;
		this.filterChain = filterChain;
	}
	
	public Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable {
		DefaultIteratorFilterChain chain = new DefaultIteratorFilterChain(filters, filterChain);
		return chain.doFilter(invoker, args);
	}

}
