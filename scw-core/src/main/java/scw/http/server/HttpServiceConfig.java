package scw.http.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import scw.beans.annotation.AopEnable;
import scw.core.Assert;
import scw.core.utils.StringUtils;
import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;
import scw.util.XUtils;

@AopEnable(false)
public class HttpServiceConfig<V> {
	private volatile Map<String, V> configMap;
	private volatile Map<String, V> patternConfigMap;
	private StringMatcher matcher;

	public final StringMatcher getMatcher() {
		return matcher == null ? DefaultStringMatcher.getInstance() : matcher;
	}

	public void setMatcher(StringMatcher matcher) {
		Assert.requiredArgument(matcher != null, "matcher");
		if (matcher.equals(getMatcher())) {
			return;
		}

		this.matcher = matcher;
		synchronized (this) {
			Map<String, V> map = new HashMap<String, V>();
			if (configMap != null) {
				map.putAll(configMap);
			}

			if (patternConfigMap != null) {
				map.putAll(patternConfigMap);
			}

			clear();
			for (Entry<String, V> entry : map.entrySet()) {
				addMapping(entry.getKey(), entry.getValue());
			}
		}
	}

	private void init(String pattern) {
		if (getMatcher().isPattern(pattern)) {
			if (patternConfigMap == null) {
				patternConfigMap = new TreeMap<String, V>(XUtils.getComparator(getMatcher()));
			}
		} else {
			if (configMap == null) {
				configMap = new HashMap<String, V>();
			}
		}
	}

	public void addMapping(String pattern, V value) {
		Assert.requiredArgument(StringUtils.isNotEmpty(pattern), "pattern");

		synchronized (this) {
			init(pattern);
			if (getMatcher().isPattern(pattern)) {
				patternConfigMap.put(pattern, value);
			} else {
				configMap.put(pattern, value);
			}
		}
	}

	public void clear() {
		synchronized (this) {
			if (configMap != null) {
				configMap.clear();
			}

			if (patternConfigMap != null) {
				patternConfigMap.clear();
			}
		}
	}

	public V getConfig(String path) {
		V value = null;
		if (configMap != null) {
			value = configMap.get(path);
		}

		if (value != null) {
			return value;
		}

		if (patternConfigMap != null) {
			for (Entry<String, V> entry : patternConfigMap.entrySet()) {
				if (getMatcher().match(entry.getKey(), path)) {
					return entry.getValue();
				}
			}
		}
		return value;
	}

	public V getConfig(ServerHttpRequest request) {
		return getConfig(request.getPath());
	}
}
