package scw.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import scw.servlet.Request;
import scw.servlet.ServletUtils;
import scw.servlet.beans.RequestBeanFactory;

public class DefaultRequestFactory implements RequestFactory {
	private final boolean debug;

	public DefaultRequestFactory(boolean debug) {
		this.debug = debug;
	}

	public Request format(RequestBeanFactory requestBeanFactory, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		if (ServletUtils.isJsonRequest(httpServletRequest)) {
			return new JsonRequest(requestBeanFactory, httpServletRequest, httpServletResponse, debug);
		} else {
			return new FormRequest(requestBeanFactory, httpServletRequest, httpServletResponse, debug, false);
		}
	}
}
