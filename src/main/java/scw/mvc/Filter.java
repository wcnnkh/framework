package scw.mvc;

public interface Filter {
	Object doFilter(Channel channel, FilterChain chain) throws Throwable;
}
