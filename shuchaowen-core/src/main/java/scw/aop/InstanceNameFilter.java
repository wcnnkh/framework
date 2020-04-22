package scw.aop;

import scw.core.instance.NoArgsInstanceFactory;

public class InstanceNameFilter implements Filter {

	private String name;
	private NoArgsInstanceFactory instanceFactory;

	public InstanceNameFilter(String name, NoArgsInstanceFactory instanceFactory) {
		this.name = name;
		this.instanceFactory = instanceFactory;
	}

	public Object doFilter(Invoker invoker, Context context, FilterChain filterChain) throws Throwable {
		if (instanceFactory.isInstance(name)) {
			return filterChain.doFilter(invoker, context);
		}

		Filter filter = instanceFactory.getInstance(name);
		return filter.doFilter(invoker, context, filterChain);
	}
}
