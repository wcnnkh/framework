package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.json.JSONObject;
import scw.json.JSONParseSupport;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.parameter.Body;

public final class JSONHttpRequest extends AbstractHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(JSONHttpRequest.class);
	private JSONObject json;

	public JSONHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue, JSONParseSupport jsonParseSupport, boolean debug, boolean require) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug, require);
		Body body = getBean(Body.class);
		if (debug) {
			debug("servletPath={},method={},{}", getServletPath(), getMethod(), body.getBody());
		}
		json = jsonParseSupport.parseObject(body.getBody());
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public String getParameter(String name) {
		if (json == null) {
			return null;
		}

		String v = json.getString(name);
		if (v == null) {
			v = super.getParameter(name);
		}
		return v;
	}

	public void appendLogger(Appendable appendable) throws IOException {
		appendable.append("servletPath=").append(getServletPath());
		appendable.append(",method=").append(getMethod());
		if (json != null) {
			appendable.append(",").append(json.toJSONString());
		}
	}
}
