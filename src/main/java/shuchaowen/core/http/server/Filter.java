package shuchaowen.core.http.server;

public interface Filter {
	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable;
}
