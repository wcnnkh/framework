package scw.aop;

import java.util.Collection;

import scw.core.instance.NoArgsInstanceFactory;

public class InstanceNamesFilter implements Filter {
	private Collection<String> filterNames;
	private NoArgsInstanceFactory instanceFactory;

	public InstanceNamesFilter(NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames) {
		this.filterNames = filterNames;
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(ProxyInvoker invoker, Object[] args,
			FilterChain filterChain) throws Throwable {
		if (filterNames == null || filterNames.isEmpty()) {
			return filterChain.doFilter(invoker, args);
		}

		return new InstanceFactoryIteratorFilterChain(instanceFactory,
				filterNames, filterChain).doFilter(invoker, args);
	}

}
