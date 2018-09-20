package shuchaowen.core.http.server;

public interface FilterChain{
	public void doFilter(Request request, Response response) throws Throwable;
}
