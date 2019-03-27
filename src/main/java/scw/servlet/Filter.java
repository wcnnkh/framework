package scw.servlet;

public interface Filter {
	void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable;
}
