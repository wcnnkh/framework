package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.beans.RequestBeanFactory;

public final class FormHttpRequest extends AbstractHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(FormHttpRequest.class);

	public FormHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue, boolean debug) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug);
		if (isDebugEnabled()) {
			debug("servletPath={},method={},{}", getServletPath(), getMethod(),
					JSONUtils.toJSONString(getParameterMap()));
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getParameter(Class<T> type, String name) {
		if (type.isArray()) {
			if (type.getComponentType().isAssignableFrom(String.class)) {
				return (T) getParameterValues(name);
			}
		}
		return super.getParameter(type, name);
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("servletPath=").append(getServletPath());
		appendable.append(",method=").append(getMethod());
		appendable.append(",").append(JSONUtils.toJSONString(getParameterMap()));
		return appendable.toString();
	}
}
