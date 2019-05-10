package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.json.JSONParseSupport;
import scw.servlet.Request;
import scw.servlet.ServletUtils;
import scw.servlet.beans.RequestBeanFactory;

public class DefaultRequestFactory implements RequestFactory {
	private final boolean debug;
	private final boolean cookieValue;

	public DefaultRequestFactory(boolean debug, boolean cookieValue) {
		this.debug = debug;
		this.cookieValue = cookieValue;
	}

	public Request format(JSONParseSupport jsonParseSupport, RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		if (ServletUtils.isJsonRequest(httpServletRequest)) {
			return new JsonRequest(jsonParseSupport, requestBeanFactory, httpServletRequest, httpServletResponse, debug, cookieValue);
		} else {
			return new FormRequest(jsonParseSupport, requestBeanFactory, httpServletRequest, httpServletResponse, debug, cookieValue);
		}
	}
}
