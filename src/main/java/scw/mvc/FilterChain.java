package scw.mvc;

public interface FilterChain {
	Object doFilter(Channel channel) throws Throwable;
}
