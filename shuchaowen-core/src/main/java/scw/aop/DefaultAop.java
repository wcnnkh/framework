package scw.aop;

import java.util.Arrays;

public class DefaultAop extends Aop {
	private final Iterable<Filter> filters;

	public DefaultAop(Filter... filters) {
		this(Arrays.asList(filters));
	}

	public DefaultAop(Iterable<Filter> filters) {
		this.filters = filters;
	}

	@Override
	public Iterable<Filter> getFilters() {
		return filters;
	}
}
