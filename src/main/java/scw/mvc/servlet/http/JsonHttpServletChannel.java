package scw.mvc.servlet.http;

import java.lang.reflect.Type;

import scw.beans.BeanFactory;
import scw.core.utils.StringUtils;
import scw.json.JSONObjectReadOnly;
import scw.json.JsonSupport;
import scw.json.JSONUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.parameter.Body;
import scw.net.http.Method;

@SuppressWarnings("unchecked")
public class JsonHttpServletChannel extends HttpServletChannel {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpServletChannel.class);
	private JSONObjectReadOnly jsonObjectReadOnly;

	public JsonHttpServletChannel(BeanFactory beanFactory,
			JsonSupport jsonParseSupport, boolean cookieValue, HttpRequest request, HttpResponse response,
			String jsonp) {
		super(beanFactory, jsonParseSupport, cookieValue, request, response, jsonp);
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
		if (StringUtils.isEmpty(value)) {
			value = super.getString(name);
		}
		return value;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	protected Object getObjectIsNotBean(String name, Class<?> type) {
		return jsonObjectReadOnly == null ? null : jsonObjectReadOnly.getObject(name, type);
	}

	@Override
	public Object getObject(String name, Type type) {
		return jsonObjectReadOnly == null ? null : jsonObjectReadOnly.getObject(name, type);
	}

	@Override
	public <T> T getObject(Class<T> type) {
		return jsonObjectReadOnly == null ? null
				: jsonParseSupport.parseObject(jsonObjectReadOnly.toJSONString(), type);
	}

	@Override
	public <T> T getObject(Type type) {
		return (T) (jsonObjectReadOnly == null ? null
				: jsonParseSupport.parseObject(jsonObjectReadOnly.toJSONString(), type));
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

	@Override
	public MyHttpServletRequest getRequest() {
		return super.getRequest();
	}

	@Override
	public MyHttpServletResponse getResponse() {
		return super.getResponse();
	}
}
