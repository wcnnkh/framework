package scw.servlet.http.filter;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;

public final class HeaderSpreadFilter extends LinkedList<String> implements Filter {
	private static final long serialVersionUID = 1L;
	{
		add(ServletUtils.COOKIE_HEADER_NAME);
	}

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (request instanceof HttpServletRequest) {
			for (String name : this) {
				ServletUtils.setSpreadHeader(name, ((HttpServletRequest) request).getHeader(name));
			}
		}
		filterChain.doFilter(request, response);
	}
}
