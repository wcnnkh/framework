package scw.http.server.cors;

import java.util.Map.Entry;
import java.util.TreeMap;

import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;
import scw.util.XUtils;

public class CorsRegistry {
	private TreeMap<String, Cors> corsMap;
	private final StringMatcher matcher;

	public CorsRegistry() {
		this(DefaultStringMatcher.getInstance());
	}

	public CorsRegistry(StringMatcher matcher) {
		this.matcher = matcher;
		this.corsMap = new TreeMap<String, Cors>(XUtils.getComparator(this.matcher));
	}

	public final StringMatcher getMatcher() {
		return matcher;
	}

	/**
	 * 线程不安全的
	 * 
	 * @param path
	 * @param cors
	 */
	public void addMapping(String path, Cors cors) {
		Cors corsToUse = cors.isReadyOnly() ? cors : cors.clone().readyOnly();
		corsMap.put(path, corsToUse);
	}

	public Cors getCors(String path) {
		Cors cors = corsMap.get(path);
		if (cors != null) {
			return cors;
		}

		for (Entry<String, Cors> pair : corsMap.entrySet()) {
			if (matcher.match(pair.getKey(), path)) {
				return pair.getValue();
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return corsMap.toString();
	}
}
