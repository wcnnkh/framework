package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.json.JSONArray;
import scw.json.JSONObject;
import scw.json.JSONParseSupport;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.parameter.Body;

public final class JSONHttpRequest extends AbstractHttpRequest {
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

	public Logger getLogger() {
		return logger;
	}

	@Override
	public Object getObject(String name, Class<?> type) {
		Object t;
		if (json != null) {
			if (JSONObject.class.isAssignableFrom(type)) {
				t = json.getJSONObject(name);
			} else if (JSONArray.class.isAssignableFrom(type)) {
				t = json.getJSONArray(name);
			} else {
				t = json.getObject(name, type);
			}
		}
		t = super.getObject(name, type);
		return t;
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
		StringBuilder appendable = new StringBuilder();
		appendable.append("servletPath=").append(getServletPath());
		appendable.append(",method=").append(getMethod());
		if (json != null) {
			appendable.append(",").append(json.toJSONString());
		}
		return appendable.toString();
	}
}
