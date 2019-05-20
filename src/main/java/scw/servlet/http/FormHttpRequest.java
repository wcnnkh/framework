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
		super(requestBeanFactory, httpServletRequest, cookieValue);
		if (debug) {
			StringBuilder sb = new StringBuilder();
			sb.append("servletPath=");
			sb.append(httpServletRequest.getServletPath());
			sb.append(",method=");
			sb.append(httpServletRequest.getMethod());
			sb.append(",");
			sb.append(JSONUtils.toJSONString(getParameterMap()));
			logger.debug(sb.toString());
		}
	}
}
