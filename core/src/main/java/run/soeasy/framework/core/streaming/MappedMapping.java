package run.soeasy.framework.core.streaming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
public class MappedMapping<K, V, W extends Mapping<K, V>> implements Mapping<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	protected final transient W source;
	protected volatile Map<K, ? extends V> map;
	protected final transient BinaryOperator<V> mergeFunction;
	protected final transient Supplier<Map<K, V>> mapFactory;

	private Map<K, ? extends V> getMap() {
		if (!isReloadable()) {
			return map == null ? Collections.emptyMap() : map;
		}

		if (map == null) {
			synchronized (this) {
				if (map == null) {
					map = source
							.collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue, mergeFunction, mapFactory));
				}
			}
		}
		return map;
	}

	protected boolean isReloadable() {
		if (source == null || mergeFunction == null || mapFactory == null) {
			return false;
		}
		return true;
	}

	@Override
	public Mapping<K, V> reload() {
		return isReloadable() ? new MappedMapping<>(source.reload(), mergeFunction, mapFactory) : this;
	}

	@Override
	public String toString() {
		return Objects.toString(getMap());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getMap());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MappedMapping) {
			MappedMapping<?, ?, ?> other = (MappedMapping<?, ?, ?>) obj;
			return Objects.equals(this.getMap(), other.getMap());
		}
		return Objects.equals(getMap(), obj);
	}

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return getMap().entrySet().stream().map((kv) -> KeyValue.of(kv.getKey(), kv.getValue()));
	}

	@Override
	public boolean hasKey(K key) {
		return getMap().containsKey(key);
	}
	
	@Override
	public Streamable<K> keys() {
		return Streamable.of(getMap().keySet());
	}

	@Override
	public Streamable<V> getValues(K key) {
		V value = getMap().get(key);
		return value == null ? Streamable.empty() : Streamable.singleton(value);
	}

	@Override
	public long count() {
		return getMap().size();
	}

	@Override
	public boolean isEmpty() {
		return getMap().isEmpty();
	}

	private void writeObject(ObjectOutputStream output) throws IOException {
		output.writeObject(getMap());
	}

	@SuppressWarnings("unchecked")
	private void readObject(ObjectInputStream input) throws IOException, ClassNotFoundException {
		this.map = (Map<K, V>) input.readObject();
	}

	@Override
	public boolean isMapped() {
		return true;
	}
}
