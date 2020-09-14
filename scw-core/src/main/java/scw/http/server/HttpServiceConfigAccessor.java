package scw.http.server;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public class HttpServiceConfigAccessor {
	private HttpServiceConfig<Boolean> jsonpSupportConfig;
	private HttpServiceConfig<Boolean> jsonSupportWrapperConfig;

	public HttpServiceConfig<Boolean> getJsonpSupportConfig() {
		return jsonpSupportConfig;
	}

	public void setJsonpSupportConfig(
			HttpServiceConfig<Boolean> jsonpSupportConfig) {
		this.jsonpSupportConfig = jsonpSupportConfig;
	}

	public HttpServiceConfig<Boolean> getJsonSupportWrapperConfig() {
		return jsonSupportWrapperConfig;
	}

	public void setJsonSupportWrapperConfig(
			HttpServiceConfig<Boolean> jsonSupportWrapperConfig) {
		this.jsonSupportWrapperConfig = jsonSupportWrapperConfig;
	}

	public <V> V getConfig(HttpServiceConfig<V> config, String path,
			V defaultValue) {
		if (config == null) {
			return defaultValue;
		}

		V v = config.getConfig(path);
		return v == null ? defaultValue : v;
	}

	public boolean isSupportJsonWrapper(String path) {
		return getConfig(getJsonSupportWrapperConfig(), path, true);
	}

	public boolean isSupportJsonp(String path) {
		return getConfig(getJsonpSupportConfig(), path, true);
	}
}
