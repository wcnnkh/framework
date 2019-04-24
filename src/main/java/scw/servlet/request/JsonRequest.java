package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;

import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.beans.RequestBeanFactory;
import scw.servlet.parameter.Body;

public class JsonRequest extends AbstractRequest {
	private Logger logger = LoggerFactory.getLogger(getClass());
	private JSONObject json;

	public JsonRequest(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, boolean isDebug, boolean cookieValue) throws IOException {
		super(requestBeanFactory, httpServletRequest, httpServletResponse, isDebug, cookieValue);
		Body body = getBean(Body.class);
		if (isDebug) {
			logger.debug("servletPath=" + getServletPath() + ",method=" + getMethod() + "," + body.getBody());
		}
		json = JSONObject.parseObject(body.getBody());
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
