package scw.aop;

public interface Filter {
	Object doFilter(Invoker invoker, Context context, FilterChain filterChain) throws Throwable;
}
