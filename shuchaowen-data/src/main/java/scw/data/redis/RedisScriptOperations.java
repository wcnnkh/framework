package scw.data.redis;

import java.util.List;

public interface RedisScriptOperations<K, V> {
	Object eval(K script, List<K> keys, List<V> args);
}
