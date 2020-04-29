package scw.aop;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class DefaultAop extends Aop {
	private final Collection<? extends Filter> filters;
	private final ProxyFactory proxyFactory;
	
	public DefaultAop(Filter ...filters) {
		this(Arrays.asList(filters));
	}

	public DefaultAop(Collection<? extends Filter> filters) {
		this(ProxyUtils.getProxyFactory(), filters);
	}

	public DefaultAop(ProxyFactory proxyFactory, Collection<? extends Filter> filters) {
		this.filters = filters;
		this.proxyFactory = proxyFactory;
	}

	@Override
	public Collection<Filter> getFilters() {
		if (filters == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableCollection(filters);
	}

	@Override
	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}
}
