package scw.mvc;

public interface Filter extends FilterInterface{
	Object doFilter(Channel channel, FilterChain chain) throws Throwable;
}
