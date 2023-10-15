package io.basc.framework.web.servlet.convert;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import io.basc.framework.web.ServerRequest;
import io.basc.framework.web.ServerResponse;

public interface ServletConverter {
	boolean canConvert(ServletRequest servletRequest);

	ServerRequest convert(ServletRequest servletRequest);

	boolean canConvert(ServletResponse servletResponse);

	ServerResponse convert(ServletResponse servletResponse);
}
