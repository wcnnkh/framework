package scw.servlet.mvc.http;

import java.lang.reflect.Type;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JsonObject;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.parameter.Body;
import scw.net.http.Method;
import scw.util.value.Value;

@SuppressWarnings("unchecked")
public class JsonHttpServletChannel extends HttpServletChannel {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpServletChannel.class);
	private JsonObject jsonObject;

	public JsonHttpServletChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport,
			MyHttpServletRequest request, MyHttpServletResponse response) {
		super(beanFactory, jsonParseSupport, request, response);
		if (Method.GET.name().equals(request.getMethod())) {
			logger.warn("servletPath={},method={}不能使用JSON类型的请求", request.getControllerPath(), request.getMethod());
		} else {
			String content = getBean(Body.class).getBody();
			if (isLogEnabled()) {
				log("requestPath={},method={},{}", request.getControllerPath(), request.getMethod(), content);
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
	public Value get(String key) {
		return jsonObject == null? null:jsonObject.get(key);
	}

	@Override
	protected Object getObjectIsNotBean(String name, Class<?> type) {
		return jsonObject == null ? null : jsonObject.getObject(name, type);
	}
	
	@Override
	protected Object getObjectSupport(String key, Type type) {
		return jsonObject == null ? null : jsonObject.getObject(key, type);
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

	@Override
	public String[] getStringArray(String key) {
		return jsonObject == null? null:jsonObject.getObject(key, String[].class);
	}
}
