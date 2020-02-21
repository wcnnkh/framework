package scw.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public abstract class AbstractServletFilterChain implements FilterChain {
	private FilterChain filterChain;

	public AbstractServletFilterChain(FilterChain filterChain) {
		this.filterChain = filterChain;
	}

	public final void doFilter(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {
		Filter filter = getNextFilter(request, response);
		if (filter == null) {
			if (filterChain == null) {
				return;
			}

			filterChain.doFilter(request, response);
		} else {
			filter.doFilter(request, response, this);
		}
	}

	protected abstract Filter getNextFilter(ServletRequest request,
			ServletResponse response) throws IOException, ServletException;
}
