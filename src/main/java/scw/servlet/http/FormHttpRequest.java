package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.json.JSONUtils;
import scw.servlet.beans.RequestBeanFactory;

public class FormHttpRequest extends DefaultHttpRequest {
	private static Logger logger = LoggerFactory
			.getLogger(FormHttpRequest.class);

	public FormHttpRequest(RequestBeanFactory requestBeanFactory,
			HttpServletRequest httpServletRequest, boolean cookieValue,
			boolean debug, boolean require) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug, require);
		if (debug) {
			debug("servletPath={},method={},{}", getServletPath(), getMethod(),
					JSONUtils.toJSONString(getParameterMap()));
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("servletPath=").append(getServletPath());
		sb.append(",method=").append(getMethod());
		sb.append(",").append(JSONUtils.toJSONString(getParameterMap()));
		return sb.toString();
	}
}
