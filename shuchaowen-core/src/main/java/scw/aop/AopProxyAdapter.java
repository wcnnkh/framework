package scw.aop;

import java.util.Collection;

public class AopProxyAdapter extends AbstractProxyAdapter {
	private Collection<Filter> filters;

	public AopProxyAdapter(Collection<Filter> filters) {
		this.filters = filters;
	}

	protected Collection<Filter> getFilters() {
		return filters;
	}

	public boolean isSupport(Class<?> clazz) {
		return ProxyUtils.getProxyAdapter().isSupport(clazz);
	}

	public Proxy proxy(Class<?> clazz, Class<?>[] interfaces, Collection<? extends Filter> filters,
			FilterChain filterChain) {
		return ProxyUtils.getProxyAdapter().proxy(clazz, interfaces, getFilters(),
				new DefaultFilterChain(filters, filterChain));
	}

	public Class<?> getClass(Class<?> clazz, Class<?>[] interfaces) {
		return ProxyUtils.getProxyAdapter().getClass(clazz, interfaces);
	}

	public boolean isProxy(Class<?> clazz) {
		return ProxyUtils.getProxyAdapter().isProxy(clazz);
	}

	public Class<?> getUserClass(Class<?> clazz) {
		return ProxyUtils.getProxyAdapter().getUserClass(clazz);
	}
}
