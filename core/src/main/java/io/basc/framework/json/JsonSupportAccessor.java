package io.basc.framework.json;

public class JsonSupportAccessor {
	private JsonSupport jsonSupport;

	public JsonSupport getJsonSupport() {
		return jsonSupport == null ? JsonUtils.getSupport() : jsonSupport;
	}

	public void setJsonSupport(JsonSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}
}
