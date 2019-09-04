package scw.mvc.servlet.http;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.ParameterFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;

public class FormHttpServletChannel extends HttpServletChannel{
	private static Logger logger = LoggerFactory.getLogger(FormHttpServletChannel.class);

	public FormHttpServletChannel(BeanFactory beanFactory, boolean logEnabled,
			Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport, boolean cookieValue,
			HttpRequest request, HttpResponse response) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue, request, response);
		if (isLogEnabled()) {
			log("requestPath={},method={},{}", getRequest().getRequestPath(), getRequest().getMethod(),
					JSONUtils.toJSONString(getRequest().getParameterMap()));
		}
	}

	public Logger getLogger() {
		return logger;
	}
}
