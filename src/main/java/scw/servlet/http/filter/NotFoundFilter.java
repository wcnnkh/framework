package scw.servlet.http.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;
import scw.servlet.ServletUtils;

public final class NotFoundFilter implements Filter {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void doFilter(Request request, Response response, FilterChain filterChain) throws Throwable {
		if (!ServletUtils.isHttpServlet(request, response)) {
			return;
		}

		if (!response.isCommitted()) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			logger.warn("servletPath={},method={},status={}", httpServletRequest.getServletPath(),
					httpServletRequest.getMethod(), 404);
			((HttpServletResponse) response).sendError(404, "not found action");
		}
		filterChain.doFilter(request, response);
	}

}
