package scw.http.server;

import java.io.BufferedReader;
import java.io.IOException;

import scw.io.IOUtils;
import scw.json.EmptyJsonElement;
import scw.json.JSONSupport;
import scw.json.JSONUtils;
import scw.json.JsonElement;

/**
 * 一个json请求
 * 
 * @author shuchaowen
 *
 */
public class JsonServerHttpRequest extends CachingServerHttpRequest {
	private JsonElement json;
	private JSONSupport jsonSupport;

	public JsonServerHttpRequest(ServerHttpRequest targetRequest) throws IOException {
		super(targetRequest);
	}

	public JSONSupport getJsonSupport() {
		return jsonSupport == null ? JSONUtils.getJsonSupport() : jsonSupport;
	}

	public void setJsonSupport(JSONSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public JsonElement getJson() throws IOException {
		if (json == null) {
			BufferedReader reader = getReader();
			if (reader == null) {
				return EmptyJsonElement.INSTANCE;
			}
			String text = IOUtils.read(reader, -1);
			json = getJsonSupport().parseJson(text);
		}
		return json;
	}
}
