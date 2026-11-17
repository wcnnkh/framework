package run.soeasy.framework.data;

import run.soeasy.framework.core.exchange2.Signal;
import run.soeasy.framework.core.streaming.Mapping;

public interface Repository<K, V> extends Mapping<K, V> {
	Signal delete(K key);

	Signal update(K key, V value);

	Signal insert(K key, V value);
}