package scw.aop;

public interface Filter {
	Object doFilter(Invoker invoker, ProxyContext context, FilterChain filterChain) throws Throwable;
}
