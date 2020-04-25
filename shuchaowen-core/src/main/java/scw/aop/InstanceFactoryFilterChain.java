package scw.aop;

import java.util.Collection;
import java.util.Collections;

import scw.core.instance.NoArgsInstanceFactory;

public final class InstanceFactoryFilterChain implements FilterChain {
	private final Collection<String> filterNames;
	private final FilterChain filterChain;
	private final NoArgsInstanceFactory instanceFactory;

	public InstanceFactoryFilterChain(NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames) {
		this(instanceFactory, filterNames, null);
	}

	@SuppressWarnings("unchecked")
	public InstanceFactoryFilterChain(NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames, FilterChain filterChain) {
		this.filterNames = filterNames == null ? Collections.EMPTY_LIST
				: filterNames;
		this.instanceFactory = instanceFactory;
		this.filterChain = filterChain;
	}

	public Object doFilter(Invoker invoker, ProxyContext context) throws Throwable {
		InstanceFactoryIteratorFilterChain chain = new InstanceFactoryIteratorFilterChain(
				instanceFactory, filterNames, filterChain);
		return chain.doFilter(invoker, context);
	}

}
