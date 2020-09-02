package scw.http.server.cors;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import scw.util.KeyValuePair;
import scw.util.StringMatcher;

public class CorsRegistry {
	private Map<String, Cors> corsMap = new HashMap<String, Cors>();
	private TreeSet<KeyValuePair<String, Cors>> matchers = new TreeSet<KeyValuePair<String, Cors>>(new Comparator<KeyValuePair<String, Cors>>() {

		public int compare(KeyValuePair<String, Cors> o1, KeyValuePair<String, Cors> o2) {
			if(matcher.isPattern(o1.getKey()) && matcher.isPattern(o2.getKey())){
				if(matcher.match(o1.getKey(), o2.getKey())){
					return 1;
				}else if(matcher.match(o2.getKey(), o1.getKey())){
					return -1;
				}else{
					return -1;
				}
			}else if(matcher.isPattern(o1.getKey())){
				return 1;
			}else if(matcher.isPattern(o2.getKey())){
				return -1;
			}
			return o1.getKey().equals(o1.getKey())? 0:-1;
		}
	});
	private final StringMatcher matcher;

	public CorsRegistry(StringMatcher matcher) {
		this.matcher = matcher;
	}

	public final StringMatcher getMatcher() {
		return matcher;
	}

	public void addMapping(String path, Cors cors) {
		if (matcher.isPattern(path)) {
			matchers.add(new KeyValuePair<String, Cors>(path, cors));
		} else {
			corsMap.put(path, cors);
		}
	}

	public Cors getCors(String path) {
		Cors cors = corsMap.get(path);
		if (cors != null) {
			return cors;
		}

		for (KeyValuePair<String, Cors> pair : matchers) {
			if (matcher.match(pair.getKey(), path)) {
				return pair.getValue();
			}
		}
		return null;
	}
}
