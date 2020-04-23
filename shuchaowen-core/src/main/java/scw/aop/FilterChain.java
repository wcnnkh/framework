package scw.aop;

public interface FilterChain {
	Object doFilter(Invoker invoker, Context context) throws Throwable;
}
