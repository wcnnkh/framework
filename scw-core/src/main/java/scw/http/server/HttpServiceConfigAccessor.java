package scw.http.server;

import scw.beans.annotation.AopEnable;
import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;

@AopEnable(false)
public class HttpServiceConfigAccessor {
	private final HttpServiceConfig<Boolean> jsonpSupportConfig;
	private final HttpServiceConfig<Boolean> jsonSupportWrapperConfig;

	public HttpServiceConfigAccessor(){
		this(DefaultStringMatcher.getInstance());
	}
	
	public HttpServiceConfigAccessor(StringMatcher matcher) {
		this.jsonSupportWrapperConfig = new HttpServiceConfig<Boolean>(matcher);
		this.jsonpSupportConfig = new HttpServiceConfig<Boolean>(matcher);
	}

	public final HttpServiceConfig<Boolean> getJsonpSupportConfig() {
		return jsonpSupportConfig;
	}

	public final HttpServiceConfig<Boolean> getJsonSupportWrapperConfig() {
		return jsonSupportWrapperConfig;
	}

	public <V> V getConfig(HttpServiceConfig<V> config, ServerHttpRequest request, V defaultValue) {
		if (config == null) {
			return defaultValue;
		}

		V v = config.getConfig(request);
		return v == null ? defaultValue : v;
	}

	public boolean isSupportJsonWrapper(ServerHttpRequest request) {
		return getConfig(getJsonSupportWrapperConfig(), request, true);
	}

	public boolean isSupportJsonp(ServerHttpRequest request) {
		return getConfig(getJsonpSupportConfig(), request, true);
	}
}
