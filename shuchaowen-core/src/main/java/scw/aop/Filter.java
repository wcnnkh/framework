package scw.aop;

import scw.beans.annotation.Bean;

@Bean(proxy = false)
public interface Filter {
	Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable;
}
