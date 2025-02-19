package io.basc.framework.web;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import io.basc.framework.core.Constants;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.server.ServerHttpRequest;
import io.basc.framework.json.EmptyJsonElement;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonElement;
import io.basc.framework.json.JsonObject;
import io.basc.framework.json.JsonSupport;
import io.basc.framework.json.JsonUtils;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LogManager;

/**
 * 一个json请求
 * 
 * @author wcnnkh
 *
 */
public class JsonServerHttpRequest extends CachingServerHttpRequest {
	private static Logger logger = LogManager.getLogger(JsonServerHttpRequest.class);
	private JsonSupport jsonSupport;

	public JsonServerHttpRequest(ServerHttpRequest targetRequest) {
		super(targetRequest);
	}

	public JsonSupport getJsonSupport() {
		return jsonSupport == null ? JsonUtils.getSupport() : jsonSupport;
	}

	public void setJsonSupport(JsonSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	private Object json;

	private Object getJson() {
		if (json == null) {
			String charsetName = getCharacterEncoding();
			if (charsetName == null) {
				charsetName = Constants.UTF_8.name();
			}

			byte[] bytes = null;
			try {
				bytes = getBytes();
			} catch (IOException e) {
				logger.error(e, "Unable to get request body");
			}

			if (bytes == null) {
				json = EmptyJsonElement.INSTANCE;
				return json;
			}

			String text;
			try {
				text = new String(bytes, charsetName);
			} catch (UnsupportedEncodingException e) {
				logger.error(e, "Unsupported character {}", charsetName);
				json = EmptyJsonElement.INSTANCE;
				return json;
			}

			JsonElement jsonElement = getJsonSupport().parseJson(text);
			if (jsonElement.isJsonArray()) {
				json = jsonElement.getAsJsonArray();
			} else if (jsonElement.isJsonObject()) {
				json = jsonElement.getAsJsonObject();
			} else {
				json = jsonElement;
			}
		}
		return json;
	}

	/**
	 * @return 如果不是一个JsonObject, 那么返回空
	 */
	public JsonObject getJsonObject() {
		Object json = getJson();
		if (json instanceof JsonObject) {
			return (JsonObject) json;
		}
		return null;
	}

	/**
	 * @return 如果不是一个JsonArray, 那么返回空
	 */
	public JsonArray getJsonArray() {
		Object json = getJson();
		if (json instanceof JsonArray) {
			return (JsonArray) json;
		}
		return null;
	}

	/**
	 * @return 如果不是一个JsonElement, 那么返回空
	 */
	public JsonElement getJsonElement() {
		Object json = getJson();
		if (json instanceof JsonElement) {
			return (JsonElement) json;
		}
		return null;
	}

	@Override
	public String toString() {
		if (getMethod() == HttpMethod.GET) {
			return super.toString();
		}

		String body = json == null ? null : json.toString();
		if (StringUtils.isEmpty(body)) {
			return super.toString();
		}

		return super.toString() + " body->" + body;
	}
}
