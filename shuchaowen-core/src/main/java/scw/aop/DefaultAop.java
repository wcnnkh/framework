package scw.aop;

import java.util.Arrays;

public class DefaultAop extends Aop {
	private final Iterable<MethodInterceptor> filters;

	public DefaultAop(MethodInterceptor... filters) {
		this(Arrays.asList(filters));
	}

	public DefaultAop(Iterable<MethodInterceptor> filters) {
		this.filters = filters;
	}

	@Override
	public Iterable<MethodInterceptor> getFilters() {
		return filters;
	}
}
