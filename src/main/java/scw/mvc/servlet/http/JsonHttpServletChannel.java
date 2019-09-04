package scw.mvc.servlet.http;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.json.JSONObjectReadOnly;
import scw.json.JSONParseSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.ParameterFilter;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.http.parameter.Body;
import scw.net.http.Method;

public class JsonHttpServletChannel extends HttpServletChannel {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpServletChannel.class);
	private JSONObjectReadOnly jsonObjectReadOnly;

	public JsonHttpServletChannel(BeanFactory beanFactory, boolean logEnabled,
			Collection<ParameterFilter> parameterFilters, JSONParseSupport jsonParseSupport, boolean cookieValue,
			HttpRequest request, HttpResponse response) {
		super(beanFactory, logEnabled, parameterFilters, jsonParseSupport, cookieValue, request, response);
		if (Method.GET.name().equals(request.getMethod())) {
			logger.warn("servletPath={},method={}不能使用JSON类型的请求", request.getRequestPath(), request.getMethod());
		} else {
			String content = getBean(Body.class).getBody();
			if (isLogEnabled()) {
				log("requestPath={},method={},{}", request.getRequestPath(), request.getMethod(), content);
			}

			if (content != null) {
				this.jsonObjectReadOnly = JSONUtils.parseObject(content);
			}
		}
	}

	@Override
	public String getString(String name) {
		String value = jsonObjectReadOnly == null ? null : jsonObjectReadOnly.getString(name);
		if (!verification(value)) {
			value = super.getString(name);
		}
		return value;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("requestPath=").append(getRequest().getRequestPath());
		appendable.append(",method=").append(getRequest().getMethod());
		if (jsonObjectReadOnly != null) {
			appendable.append(",").append(jsonObjectReadOnly.toJSONString());
		}
		return appendable.toString();
	}
}
