package scw.mvc;

public interface FilterChain {
	void doFilter(Channel channel) throws Throwable;
}
