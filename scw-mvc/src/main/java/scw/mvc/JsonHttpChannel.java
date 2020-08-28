package scw.mvc;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import scw.beans.BeanFactory;
import scw.http.HttpMethod;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;
import scw.json.JSONSupport;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mvc.parameter.Body;

public class JsonHttpChannel<R extends ServerHttpRequest, P extends ServerHttpResponse>
		extends AbstractHttpChannel<R, P> {
	private static Logger logger = LoggerFactory.getLogger(JsonHttpChannel.class);
	private JsonObject jsonObject;

	public JsonHttpChannel(BeanFactory beanFactory, JSONSupport jsonParseSupport, R request, P response) {
		super(beanFactory, jsonParseSupport, request, response);
		if (HttpMethod.GET.name().equals(request.getMethod())) {
			logger.warn("path={},method={}不能使用JSON类型的请求", request.getPath(), request.getMethod());
		} else {
			String content = getBean(Body.class).getBody();
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
		if (value == null && jsonObject != null) {
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

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] getArray(String name, Class<? extends E> type) {
		if (jsonObject == null) {
			return super.getArray(name, type);
		}

		JsonArray jsonArray = jsonObject.getJsonArray(name);
		if (jsonArray == null || jsonArray.isEmpty()) {
			return (E[]) Array.newInstance(type, 0);
		}

		Object array = Array.newInstance(type, jsonArray.size());
		for (int i = 0, len = jsonArray.size(); i < len; i++) {
			Array.set(array, i, jsonArray.getObject(i, type));
		}
		return (E[]) array;
	}
}
