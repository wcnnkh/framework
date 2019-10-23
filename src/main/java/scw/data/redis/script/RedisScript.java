package scw.data.redis.script;

import java.util.List;

public interface RedisScript<K, V> {
	String getScript();

	List<K> getKeyList();

	List<V> getValueList();
}
