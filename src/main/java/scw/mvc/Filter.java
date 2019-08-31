package scw.mvc;

public interface Filter {
	void doFilter(Channel channel, FilterChain chain) throws Throwable;
}
