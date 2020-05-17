package scw.net.http.server.mvc;

import java.lang.reflect.Type;

import scw.beans.BeanFactory;
import scw.json.JSONSupport;
import scw.json.JsonObject;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.net.http.HttpMethod;
import scw.net.http.server.ServerHttpRequest;
import scw.net.http.server.ServerHttpResponse;
import scw.net.http.server.mvc.parameter.Body;

public class JsonHttpChannel<R extends ServerHttpRequest, P extends ServerHttpResponse> extends AbstractHttpChannel<R, P> {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpChannel.class);
	private JsonObject jsonObject;

	public JsonHttpChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport,
			R request, P response) {
		super(beanFactory, jsonParseSupport, request, response);
		if (HttpMethod.GET.name().equals(request.getMethod())) {
			logger.warn("path={},method={}不能使用JSON类型的请求", request.getPath(), request.getMethod());
		} else {
			String content = getHttpChannelBeanManager().getBean(Body.class).getBody();
			if (isLogEnabled()) {
				log("path={},method={},{}", request.getPath(), request.getMethod(), content);
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
		appendable.append("path=").append(getRequest().getPath());
		appendable.append(",method=").append(getRequest().getMethod());
		if (jsonObject != null) {
			appendable.append(",").append(jsonObject.toJsonString());
		}
		return appendable.toString();
	}
	
	@Override
	public String[] getStringArray(String key) {
		return jsonObject == null? null:jsonObject.getObject(key, String[].class);
	}
}
