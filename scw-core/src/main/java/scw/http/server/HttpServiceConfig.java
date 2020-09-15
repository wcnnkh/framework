package scw.http.server;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import scw.core.Assert;
import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;
import scw.util.XUtils;

public class HttpServiceConfig<V> {
	private volatile Map<String, V> configMap;
	private final StringMatcher matcher;

	public HttpServiceConfig() {
		this(DefaultStringMatcher.getInstance());
	}

	public HttpServiceConfig(StringMatcher matcher) {
		Assert.requiredArgument(matcher != null, "matcher");
		this.matcher = matcher;
		this.configMap = new TreeMap<String, V>(XUtils.getComparator(matcher));
	}

	public final StringMatcher getMatcher() {
		return matcher;
	}

	public HttpServiceConfig<V> addMapping(String pattern, V value) {
		synchronized (configMap) {
			configMap.put(pattern, value);
		}
		return this;
	}

	public HttpServiceConfig<V> clear() {
		synchronized (configMap) {
			configMap.clear();
		}
		return this;
	}

	public V getConfig(String path) {
		V value = configMap.get(path);
		if (value != null) {
			return value;
		}

		for (Entry<String, V> entry : configMap.entrySet()) {
			if (getMatcher().match(entry.getKey(), path)) {
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return configMap.toString();
	}
}
