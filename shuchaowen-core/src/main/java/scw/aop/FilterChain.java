package scw.aop;

public interface FilterChain {
	Object doFilter(Invoker invoker, ProxyContext context) throws Throwable;
}
