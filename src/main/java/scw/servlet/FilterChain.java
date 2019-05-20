package scw.servlet;

public interface FilterChain {
	void doFilter(Request request, Response response)
			throws Throwable;
}
