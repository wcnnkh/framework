package scw.servlet.service;

import scw.beans.annotation.Bean;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.Filter;
import scw.servlet.FilterChain;
import scw.servlet.Request;
import scw.servlet.Response;

@Bean(proxy=false)
public class NotFoundService implements Filter {
	private Logger logger = LoggerFactory.getLogger(getClass());

	public void doFilter(Request request, Response response,
			FilterChain filterChain) throws Throwable {
		if (!response.isCommitted()) {
			logger.warn("servletPath={},method={},status={}", request.getServletPath(), request.getMethod(), 404);
			response.sendError(404, "not found action");
		}
		filterChain.doFilter(request, response);
	}

}
