package scw.servlet.http.filter;

import javax.servlet.http.HttpServletResponse;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
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

		logger.warn("not found：{}", request);
		if (!response.isCommitted() && response instanceof HttpServletResponse) {
			((HttpServletResponse) response).sendError(404, "not found action");
		}
		filterChain.doFilter(request, response);
	}

}
