package run.soeasy.framework.core.collection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.KeyValue;
import run.soeasy.framework.core.collection.KeyValueListable.KeyValueListableWrapper;

@RequiredArgsConstructor
public class RandomAccessKeyValues<K, V, E extends KeyValue<K, V>, W extends KeyValueListable<K, V, E>>
		implements KeyValueListable<K, V, E>, KeyValueListableWrapper<K, V, E, W> {
	@NonNull
	@Getter
	private final W source;
	private volatile transient Map<K, List<V>> randomAccessMap;
	private volatile int size;

	public boolean reload(boolean force) {
		if (force || randomAccessMap == null) {
			synchronized (this) {
				if (force || randomAccessMap == null) {
					Map<K, List<V>> map = new LinkedHashMap<>();
					int size = 0;
					for (KeyValue<K, V> keyValue : source.getElements()) {
						List<V> list = map.get(keyValue.getKey());
						if (list == null) {
							list = new ArrayList<>();
							map.put(keyValue.getKey(), list);
						}
						list.add(keyValue.getValue());
						size++;
					}
					this.size = size;
					// 优化内存
					for (Entry<K, List<V>> entry : map.entrySet()) {
						entry.setValue(CollectionUtils.newReadOnlyList(entry.getValue()));
					}
					randomAccessMap = map;
					return true;
				}
			}
		}
		return false;
	}

	public int size() {
		reload(false);
		return size;
	}

	@Override
	public Elements<V> getValues(K key) {
		reload(false);
		List<V> list = randomAccessMap.get(key);
		return list == null ? Elements.empty() : Elements.of(list);
	}

	@Override
	public Elements<K> keys() {
		reload(false);
		return Elements.of(randomAccessMap.keySet());
	}

	@Override
	public boolean hasKey(K key) {
		reload(false);
		return randomAccessMap.containsKey(key);
	}
}
