package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.json.JSONUtils;
import scw.servlet.beans.RequestBeanFactory;

public class FormHttpRequest extends DefaultHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(FormHttpRequest.class);

	public FormHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue, boolean debug) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug);
		if (debug) {
			debug("servletPath={},method={},{}", httpServletRequest.getServletPath(), httpServletRequest.getMethod(),
					JSONUtils.toJSONString(getParameterMap()));
		}
	}

	@Override
	public Logger getLogger() {
		return logger;
	}
}
