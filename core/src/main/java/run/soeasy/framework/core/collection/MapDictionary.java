package run.soeasy.framework.core.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
public class MapDictionary<K, V, E extends KeyValue<K, V>, W extends Dictionary<K, V, E>>
		implements Dictionary<K, V, E>, DictionaryWrapper<K, V, E, W> {
	@NonNull
	@Getter
	private final W source;
	private volatile Map<K, List<V>> map;
	@NonNull
	private final MapFactory<K, List<V>, Map<K, List<V>>> mapFactory;
	@Getter
	private final boolean uniqueness;

	public MapDictionary(W source, boolean orderly, boolean uniqueness) {
		this(source, (a, b) -> orderly ? new LinkedHashMap<>(a, b) : new HashMap<>(a, b), uniqueness);
	}

	public boolean reload(boolean force) {
		if (force || map == null) {
			synchronized (this) {
				if (force || map == null) {
					Map<K, List<V>> map = mapFactory.create();
					for (KeyValue<K, V> keyValue : source.getElements()) {
						List<V> list = map.get(keyValue.getKey());
						if (list == null) {
							list = new ArrayList<>(4);
							map.put(keyValue.getKey(), list);
						}
						list.add(keyValue.getValue());
					}

					for (Entry<K, List<V>> entry : map.entrySet()) {
						if (uniqueness) {
							if (entry.getValue().size() != 1) {
								throw new NoUniqueElementException(String.valueOf(entry.getKey()));
							}
							entry.setValue(Arrays.asList(entry.getValue().get(0)));
						} else {
							entry.setValue(CollectionUtils.newReadOnlyList(entry.getValue()));
						}
					}
					this.map = mapFactory.display(map);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Dictionary<K, V, E> asMap(boolean uniqueness) {
		return this.uniqueness == uniqueness ? this : getSource().asMap(uniqueness);
	}

	@Override
	public boolean isMap() {
		return true;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	public Map<K, List<V>> getMap() {
		reload(false);
		return map;
	}

	@Override
	public int size() {
		return uniqueness ? getMap().size() : getMap().entrySet().stream().mapToInt((e) -> e.getValue().size()).sum();
	}

	@Override
	public boolean hasElements() {
		return getMap().isEmpty();
	}

	@Override
	public Elements<V> getValues(K key) {
		List<V> list = getMap().get(key);
		return list == null ? Elements.empty() : Elements.of(list);
	}

	@Override
	public Elements<K> keys() {
		return Elements.of(getMap().keySet());
	}

	@Override
	public boolean hasKey(K key) {
		return getMap().containsKey(key);
	}
}
