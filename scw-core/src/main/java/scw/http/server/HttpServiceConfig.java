package scw.http.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import scw.core.Assert;
import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;
import scw.util.XUtils;

public class HttpServiceConfig<V> {
	private Map<String, V> configMap = new HashMap<String, V>();
	private Map<String, V> patternConfigMap;
	private final StringMatcher matcher;

	public HttpServiceConfig() {
		this(DefaultStringMatcher.getInstance());
	}

	public HttpServiceConfig(StringMatcher matcher) {
		Assert.requiredArgument(matcher != null, "matcher");
		this.matcher = matcher;
		this.patternConfigMap = new TreeMap<String, V>(
				XUtils.getComparator(matcher));
	}

	public final StringMatcher getMatcher() {
		return matcher;
	}

	public HttpServiceConfig<V> addMapping(String path, V value) {
		if (getMatcher().isPattern(path)) {
			patternConfigMap.put(path, value);
		} else {
			configMap.put(path, value);
		}
		return this;
	}

	public HttpServiceConfig<V> removeMapping(String path) {
		if (getMatcher().isPattern(path)) {
			patternConfigMap.remove(path);
		} else {
			configMap.remove(path);
		}
		return this;
	}

	public HttpServiceConfig<V> clear() {
		patternConfigMap.clear();
		configMap.clear();
		return this;
	}

	public V getConfig(String path) {
		if (getMatcher().isPattern(path)) {
			for (Entry<String, V> entry : patternConfigMap.entrySet()) {
				if (getMatcher().match(entry.getKey(), path)) {
					return entry.getValue();
				}
			}
			return null;
		} else {
			return configMap.get(path);
		}
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<String, Object>(4);
		map.put("configMap", configMap);
		map.put("patternConfigMap", patternConfigMap);
		return map.toString();
	}
}
