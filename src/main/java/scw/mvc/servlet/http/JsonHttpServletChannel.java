package scw.mvc.servlet.http;

import java.lang.reflect.Type;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JsonObject;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.http.HttpRequest;
import scw.mvc.http.HttpResponse;
import scw.mvc.parameter.Body;
import scw.net.http.Method;

@SuppressWarnings("unchecked")
public class JsonHttpServletChannel extends HttpServletChannel {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpServletChannel.class);
	private JsonObject jsonObject;

	public JsonHttpServletChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport, boolean cookieValue,
			HttpRequest request, HttpResponse response, String jsonp) {
		super(beanFactory, jsonParseSupport, cookieValue, request, response, jsonp);
		if (Method.GET.name().equals(request.getMethod())) {
			logger.warn("servletPath={},method={}不能使用JSON类型的请求", request.getRequestPath(), request.getMethod());
		} else {
			String content = getBean(Body.class).getBody();
			if (isLogEnabled()) {
				log("requestPath={},method={},{}", request.getRequestPath(), request.getMethod(), content);
			}

			if (content != null) {
				this.jsonObject = jsonParseSupport.parseObject(content);
			}
		}
	}

	@Override
	public String getString(String name) {
		return jsonObject == null ? null : jsonObject.getString(name);
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	protected Object getObjectIsNotBean(String name, Class<?> type) {
		return jsonObject == null ? null : jsonObject.getObject(name, type);
	}

	@Override
	public Object getObject(String name, Type type) {
		return jsonObject == null ? null : jsonObject.getObject(name, type);
	}

	@Override
	public <T> T getObject(Class<T> type) {
		return jsonObject == null ? null : jsonParseSupport.parseObject(jsonObject.toJsonString(), type);
	}

	@Override
	public <T> T getObject(Type type) {
		return (T) (jsonObject == null ? null : jsonParseSupport.parseObject(jsonObject.toJsonString(), type));
	}

	@Override
	public String toString() {
		StringBuilder appendable = new StringBuilder();
		appendable.append("requestPath=").append(getRequest().getRequestPath());
		appendable.append(",method=").append(getRequest().getMethod());
		if (jsonObject != null) {
			appendable.append(",").append(jsonObject.toJsonString());
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
