package run.soeasy.framework.core.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractMultiValueMap<K, V, M extends Map<K, List<V>>>
		implements MultiValueMap<K, V>, MapWrapper<K, List<V>, M> {
	@NonNull
	private Function<? super K, ? extends List<V>> valuesCreator = (e) -> new ArrayList<>();

	@Override
	public void adds(K key, List<V> values) {
		List<V> list = getSource().get(key);
		if (list == null) {
			list = valuesCreator.apply(key);
			getSource().put(key, list);
		}
		list.addAll(values);
	}

	@Override
	public void set(K key, V value) {
		List<V> list = valuesCreator.apply(key);
		list.add(value);
		put(key, list);
	}

	@Override
	public String toString() {
		return getSource().toString();
	}

	@Override
	public int hashCode() {
		return getSource().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AbstractMultiValueMap) {
			return getSource().equals((((AbstractMultiValueMap<?, ?, ?>) obj).getSource()));
		}
		return getSource().equals(obj);
	}
}
