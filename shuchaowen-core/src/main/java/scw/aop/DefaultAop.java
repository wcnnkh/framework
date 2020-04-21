package scw.aop;

import java.util.Collection;
import java.util.Collections;

public class DefaultAop extends Aop {
	private final Collection<Filter> filters;
	private final ProxyAdapter proxyAdapter;

	public DefaultAop(Collection<Filter> filters) {
		this(ProxyUtils.getProxyAdapter(), filters);
	}

	public DefaultAop(ProxyAdapter proxyAdapter, Collection<Filter> filters) {
		this.filters = filters;
		this.proxyAdapter = proxyAdapter;
	}

	@Override
	public Collection<Filter> getFilters() {
		if (filters == null) {
			return Collections.emptyList();
		}

		return Collections.unmodifiableCollection(filters);
	}

	@Override
	protected ProxyAdapter getProxyAdapter() {
		return proxyAdapter;
	}
}
