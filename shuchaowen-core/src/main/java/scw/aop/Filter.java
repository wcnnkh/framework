package scw.aop;

import scw.beans.annotation.Bean;

@Bean(proxy=false)
public interface Filter {
	Object doFilter(Invoker invoker, ProxyContext context, FilterChain filterChain) throws Throwable;
}
