package scw.servlet.mvc.http;

import java.lang.reflect.Type;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JsonObject;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.parameter.Body;
import scw.net.http.HttpMethod;

public class JsonHttpServletChannel extends HttpServletChannel {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpServletChannel.class);
	private JsonObject jsonObject;

	public JsonHttpServletChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport,
			MyHttpServletRequest request, MyHttpServletResponse response) {
		super(beanFactory, jsonParseSupport, request, response);
		if (HttpMethod.GET.name().equals(request.getMethod())) {
			logger.warn("servletPath={},method={}不能使用JSON类型的请求", request.getController(), request.getMethod());
		} else {
			String content = getBean(Body.class).getBody();
			if (isLogEnabled()) {
				log("requestPath={},method={},{}", request.getController(), request.getMethod(), content);
			}

			if (content != null) {
				this.jsonObject = jsonParseSupport.parseObject(content);
			}
		}
	}

	@Override
	public String getStringValue(String name) {
		String value = super.getStringValue(name);
		if(value == null && jsonObject != null){
			value = jsonObject.getString(name);
		}
		return value;
	}

	public Logger getLogger() {
		return logger;
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
