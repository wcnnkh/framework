package scw.aop;

import java.util.Collection;

import scw.aop.Context;
import scw.aop.Filter;
import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.core.instance.NoArgsInstanceFactory;

public class InstanceNamesFilter implements Filter {
	private Collection<String> filterNames;
	private NoArgsInstanceFactory instanceFactory;

	public InstanceNamesFilter(NoArgsInstanceFactory instanceFactory,
			Collection<String> filterNames) {
		this.filterNames = filterNames;
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(Invoker invoker, Context context,
			FilterChain filterChain) throws Throwable {
		if (filterNames == null || filterNames.isEmpty()) {
			return filterChain.doFilter(invoker, context);
		}

		return new InstanceFactoryIteratorFilterChain(instanceFactory,
				filterNames, filterChain).doFilter(invoker, context);
	}

}
