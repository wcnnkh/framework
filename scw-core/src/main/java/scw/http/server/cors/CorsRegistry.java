package scw.http.server.cors;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.TreeMap;

import scw.util.DefaultStringMatcher;
import scw.util.StringMatcher;

public class CorsRegistry {
	private TreeMap<String, Cors> corsMap = new TreeMap<String, Cors>(new Comparator<String>() {

		public int compare(String o1, String o2) {
			if (matcher.isPattern(o1) && matcher.isPattern(o2)) {
				if (matcher.match(o1, o2)) {
					return 1;
				} else if (matcher.match(o2, o1)) {
					return -1;
				} else {
					return -1;
				}
			} else if (matcher.isPattern(o1)) {
				return 1;
			} else if (matcher.isPattern(o2)) {
				return -1;
			}
			return o1.equals(o1) ? 0 : -1;
		}
	});
	private final StringMatcher matcher;

	public CorsRegistry() {
		this(DefaultStringMatcher.getInstance());
	}

	public CorsRegistry(StringMatcher matcher) {
		this.matcher = matcher;
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
