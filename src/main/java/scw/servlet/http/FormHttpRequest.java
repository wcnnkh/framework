package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.json.JSONUtils;
import scw.servlet.beans.RequestBeanFactory;

public final class FormHttpRequest extends AbstractHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(FormHttpRequest.class);

	public FormHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue, boolean debug, boolean require) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug, require);
		if (debug) {
			debug("servletPath={},method={},{}", getServletPath(), getMethod(),
					JSONUtils.toJSONString(getParameterMap()));
		}
	}

	public Logger getLogger() {
		return logger;
	}

	public void appendLogger(Appendable appendable) throws IOException {
		appendable.append("servletPath=").append(getServletPath());
		appendable.append(",method=").append(getMethod());
		appendable.append(",").append(JSONUtils.toJSONString(getParameterMap()));
	}
}
