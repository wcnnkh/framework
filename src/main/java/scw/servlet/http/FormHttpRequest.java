package scw.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.servlet.ServletUtils;
import scw.servlet.beans.RequestBeanFactory;

public final class FormHttpRequest extends AbstractHttpRequest {
	private static Logger logger = LoggerFactory
			.getLogger(FormHttpRequest.class);

	public FormHttpRequest(RequestBeanFactory requestBeanFactory,
			HttpServletRequest httpServletRequest, boolean cookieValue,
			boolean debug) throws IOException {
		super(requestBeanFactory, httpServletRequest, cookieValue, debug);
		if (isDebugEnabled()) {
			debug("servletPath={},method={},{}", getServletPath(), getMethod(),
					JSONUtils.toJSONString(getParameterMap()));
		}
	}

	public Logger getLogger() {
		return logger;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> type) {
		Object bean = super.getBean(type);
		if (bean == null) {
			bean = ServletUtils.getRequestObjectParameterWrapper(this, type,
					null);
		}
		return (T) bean;
	}

	@Override
	public Object getObject(String name, Class<?> type) {
		Object bean = super.getObject(name, type);
		if (bean == null) {
			bean = ServletUtils.getRequestObjectParameterWrapper(this, type,
					name);
		}
		return bean;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("servletPath=").append(getServletPath());
		appendable.append(",method=").append(getMethod());
		appendable.append(",")
				.append(JSONUtils.toJSONString(getParameterMap()));
		return appendable.toString();
	}
}
