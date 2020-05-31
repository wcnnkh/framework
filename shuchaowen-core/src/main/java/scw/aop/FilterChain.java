package scw.aop;

public interface FilterChain {
	Object doFilter(ProxyInvoker invoker, Object[] args) throws Throwable;
}
