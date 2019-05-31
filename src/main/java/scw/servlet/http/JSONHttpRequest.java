package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.json.JSONObject;
import scw.json.JSONParseSupport;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.parameter.Body;

public class JSONHttpRequest extends DefaultHttpRequest {
	private static Logger logger = LoggerFactory.getLogger(JSONHttpRequest.class);
	private JSONObject json;

	public JSONHttpRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			boolean cookieValue, JSONParseSupport jsonParseSupport, boolean debug) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug);
		Body body = getBean(Body.class);
		if (debug) {
			debug("servletPath={},method={},{}", getServletPath(), getMethod(), body.getBody());
		}
		json = jsonParseSupport.parseObject(body.getBody());
	}

	@Override
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("servletPath=").append(getServletPath());
		sb.append(",method=").append(getMethod());
		if (json != null) {
			sb.append(",").append(json.toJSONString());
		}
		return sb.toString();
	}
}
