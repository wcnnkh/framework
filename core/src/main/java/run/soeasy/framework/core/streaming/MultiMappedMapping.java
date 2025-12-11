package run.soeasy.framework.core.streaming;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.domain.KeyValue;

@RequiredArgsConstructor
@Getter
public class MultiMappedMapping<K, V, W extends Mapping<K, V>> implements Mapping<K, V>, Serializable {
	private static final long serialVersionUID = 1L;

	protected final transient W source;
	protected volatile Map<K, ? extends Collection<V>> map;
	protected final transient Supplier<Map<K, Collection<V>>> mapFactory;
	protected final transient Supplier<Collection<V>> collectionFactory;

	private Map<K, ? extends Collection<V>> getMap() {
		if (!isReloadable()) {
			return map == null ? Collections.emptyMap() : map;
		}
		if (map == null) {
			synchronized (this) {
				if (map == null) {
					this.map = source.collect(Collectors.groupingBy(KeyValue::getKey, mapFactory,
							Collectors.mapping(KeyValue::getValue, Collectors.toCollection(collectionFactory))));
				}
			}
		}
		return map;
	}

	protected boolean isReloadable() {
		if (source == null || mapFactory == null || collectionFactory == null) {
			return false;
		}
		return true;
	}

	@Override
	public Mapping<K, V> reload() {
		return isReloadable() ? new MultiMappedMapping<>(source.reload(), mapFactory, collectionFactory) : this;
	}

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return getMap().entrySet().stream()
				.flatMap(entry -> entry.getValue().stream().map(value -> KeyValue.of(entry.getKey(), value)));
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
		Collection<V> values = getMap().get(key);
		return values == null ? Streamable.empty() : Streamable.of(values);
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
		this.map = (Map<K, Collection<V>>) input.readObject();
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
		if (obj instanceof MultiMappedMapping) {
			MultiMappedMapping<?, ?, ?> other = (MultiMappedMapping<?, ?, ?>) obj;
			return Objects.equals(this.getMap(), other.getMap());
		}
		return Objects.equals(this.getMap(), obj);
	}

	@Override
	public boolean isMapped() {
		return true;
	}
}