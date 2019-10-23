package scw.data.redis.script;

import java.io.Serializable;
import java.util.List;

public class SimplRedisScript<K, V> implements RedisScript<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final String script;
	private final List<K> keyList;
	private final List<V> valueList;

	public SimplRedisScript(String script, List<K> keyList, List<V> valueList) {
		this.script = script;
		this.keyList = keyList;
		this.valueList = valueList;
	}

	public String getScript() {
		return script;
	}

	public List<K> getKeyList() {
		return keyList;
	}

	public List<V> getValueList() {
		return valueList;
	}

}
