package scw.aop;

import java.util.Arrays;

import scw.instance.ServiceLoaderFactory;

public class DefaultAop extends Aop {
	private final Iterable<MethodInterceptor> filters;

	public DefaultAop(MethodInterceptor... filters) {
		this(Arrays.asList(filters));
	}

	public DefaultAop(Iterable<MethodInterceptor> filters) {
		this.filters = filters;
	}
	
	public DefaultAop(ServiceLoaderFactory serviceLoaderFactory){
		this(serviceLoaderFactory.getServiceLoader(MethodInterceptor.class));
	}

	@Override
	public Iterable<MethodInterceptor> getFilters() {
		return filters;
	}
}
