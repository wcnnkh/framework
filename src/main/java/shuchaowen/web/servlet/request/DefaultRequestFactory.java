package shuchaowen.web.servlet.request;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import shuchaowen.web.servlet.Request;
import shuchaowen.web.servlet.bean.RequestBeanFactory;

public class DefaultRequestFactory implements RequestFactory {
	private static final String FORM_CONTENT_TYPE_PREFIX = "application/x-www-form-urlencoded";
	private static final String JSON_CONTENT_TYPE_PREFIX = "application/json";
	private final boolean debug;

	public DefaultRequestFactory(boolean debug) {
		this.debug = debug;
	}

	public Request format(RequestBeanFactory requestBeanFactory,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws IOException {
		if (httpServletRequest.getContentType() == null
				|| httpServletRequest.getContentType().startsWith(
						FORM_CONTENT_TYPE_PREFIX)) {
			return new FormRequest(requestBeanFactory, httpServletRequest,
					httpServletResponse, debug, false);
		} else if (httpServletRequest.getContentType().startsWith(
				JSON_CONTENT_TYPE_PREFIX)) {
			return new JsonRequest(requestBeanFactory, httpServletRequest,
					httpServletResponse, debug);
		} else {
			return new FormRequest(requestBeanFactory, httpServletRequest,
					httpServletResponse, debug, false);
		}
	}
}
