package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.json.JSONObject;
import scw.json.JSONParseSupport;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.parameter.Body;

public class JsonRequest extends AbstractRequest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private JSONObject json;

	public JsonRequest(JSONParseSupport jsonParseSupport, RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug, boolean cookieValue) throws IOException {
		super(jsonParseSupport, requestBeanFactory, httpServletRequest, httpServletResponse, isDebug, cookieValue);
		Body body = getBean(Body.class);
		if (isDebug) {
			logger.debug("servletPath=" + getServletPath() + ",method=" + getMethod() + "," + body.getBody());
		}
		json = jsonParseSupport.parseObject(body.getBody());
	}

	public JSONObject getJson() {
		return json;
	}

	@Override
	public String getValue(String key, boolean cookie) {
		String v = json.getString(key);
		if (v == null) {
			v = super.getValue(key, cookie);
		}
		return v;
	}
}
