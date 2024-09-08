package io.basc.framework.observe.register;

import java.util.Map;

import io.basc.framework.util.observe.event.ChangeEvent;
import io.basc.framework.util.observe_old.Observer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = false)
public class ObservableEntry<K, V> extends Observer<ChangeEvent<V>> implements Map.Entry<K, V> {
	private final K key;
	private V value;

	@Override
	public V setValue(V value) {
		V previous = this.value;
		this.value = value;
		publishEvent(new ChangeEvent<V>(this, previous, value));
		return previous;
	}
}
