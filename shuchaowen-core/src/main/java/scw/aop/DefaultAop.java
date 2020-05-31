package scw.aop;


public class DefaultAop extends Aop {
	private final Filter[] filters;
	private final ProxyFactory proxyFactory;
	
	public DefaultAop(Filter... filters) {
		this(ProxyUtils.getProxyFactory(), filters);
	}

	public DefaultAop(ProxyFactory proxyFactory, Filter[] filters) {
		this.filters = filters;
		this.proxyFactory = proxyFactory;
	}
	
	@Override
	public Filter[] getFilters() {
		return filters.clone();
	}

	@Override
	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}
}
