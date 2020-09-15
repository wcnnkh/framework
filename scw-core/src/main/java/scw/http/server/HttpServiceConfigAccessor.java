package scw.http.server;

import scw.beans.annotation.AopEnable;

@AopEnable(false)
public class HttpServiceConfigAccessor {
	private final HttpServiceConfig<Boolean> jsonpSupportConfig = new HttpServiceConfig<Boolean>();
	private final HttpServiceConfig<Boolean> jsonSupportWrapperConfig = new HttpServiceConfig<Boolean>();

	public final HttpServiceConfig<Boolean> getJsonpSupportConfig() {
		return jsonpSupportConfig;
	}

	public final HttpServiceConfig<Boolean> getJsonSupportWrapperConfig() {
		return jsonSupportWrapperConfig;
	}

	public <V> V getConfig(HttpServiceConfig<V> config, String path, V defaultValue) {
		if (config == null) {
			return defaultValue;
		}

		V v = config.getConfig(path);
		return v == null ? defaultValue : v;
	}

	public <V> V getConfig(HttpServiceConfig<V> config, ServerHttpRequest request, V defaultValue) {
		return getConfig(config, request.getPath(), defaultValue);
	}

	public boolean isSupportJsonWrapper(ServerHttpRequest request) {
		return getConfig(getJsonSupportWrapperConfig(), request, true);
	}

	public boolean isSupportJsonp(ServerHttpRequest request) {
		return getConfig(getJsonpSupportConfig(), request, true);
	}
}
