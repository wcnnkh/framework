package io.basc.framework.web.servlet.http;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.basc.framework.web.ServerHttpRequest;
import io.basc.framework.web.ServerHttpResponse;
import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;
import io.basc.framework.web.servlet.convert.ServletConverter;

public interface HttpServletConverter extends ServletConverter {
	@Override
	default boolean canConvert(ServletRequest servletRequest) {
		return servletRequest instanceof HttpServletRequest && canConvert((HttpServletRequest) servletRequest);
	}

	boolean canConvert(HttpServletRequest httpServletRequest);

	@Override
	default ServerRequest convert(ServletRequest servletRequest) {
		return convert((HttpServletRequest) servletRequest);
	}

	ServerHttpRequest convert(HttpServletRequest httpServletRequest);

	@Override
	default boolean canConvert(ServletResponse servletResponse) {
		return servletResponse instanceof HttpServletResponse && canConvert((HttpServletResponse) servletResponse);
	}

	@Override
	default ServerResponse convert(ServletResponse servletResponse) {
		return convert((HttpServletResponse) servletResponse);
	}

	boolean canConvert(HttpServletResponse httpServletResponse);

	ServerHttpResponse convert(HttpServletResponse httpServletResponse);
}
