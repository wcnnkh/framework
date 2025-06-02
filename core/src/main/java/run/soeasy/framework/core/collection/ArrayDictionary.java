package run.soeasy.framework.core.collection;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;

public class ArrayDictionary<K, V, E extends KeyValue<K, V>, W extends Dictionary<K, V, E>>
		implements Dictionary<K, V, E>, DictionaryWrapper<K, V, E, W> {
	@NonNull
	@Getter
	private final W source;
	private volatile List<E> list;
	@Getter
	private final boolean uniqueness;

	public ArrayDictionary(@NonNull W source, boolean uniqueness) {
		this.source = source;
		this.uniqueness = uniqueness;
	}

	public boolean reload(boolean force) {
		if (force || this.list == null) {
			synchronized (this) {
				if (force || this.list == null) {
					List<E> list;
					if (this.uniqueness) {
						Map<K, E> map = new LinkedHashMap<>();
						for (E element : source.getElements()) {
							if (map.containsKey(element.getKey())) {
								throw new NoUniqueElementException(String.valueOf(element.getKey()));
							}
							map.put(element.getKey(), element);
						}
						list = map.entrySet().stream().map((e) -> e.getValue()).collect(Collectors.toList());
					} else {
						list = source.getElements().toList();
					}
					this.list = CollectionUtils.newReadOnlyList(list);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Dictionary<K, V, E> asArray(boolean uniqueness) {
		return this.uniqueness == uniqueness ? this : getSource().asArray(uniqueness);
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public boolean isMap() {
		return false;
	}

	public List<E> getList() {
		reload(false);
		return list;
	}

	@Override
	public int size() {
		return getList().size();
	}

	@Override
	public boolean hasElements() {
		return !getList().isEmpty();
	}

	@Override
	public Elements<K> keys() {
		return Elements.of(getList()).map((e) -> e.getKey());
	}
}
