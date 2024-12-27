package io.basc.framework.core.convert.transform.collection;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import io.basc.framework.core.convert.transform.Accesstor;
import io.basc.framework.core.convert.transform.Mapping;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValues;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class KeyValuesMapping<K, V extends Accesstor> implements Mapping<K, V>, KeyValues<K, V> {
	@NonNull
	private final Supplier<? extends Elements<K>> keysSupplier;
	@NonNull
	private final Function<? super K, ? extends V> creator;

	@Override
	public Elements<V> getAccesstors(K key) {
		V access = creator.apply(key);
		return Elements.singleton(access);
	}

	@Override
	public Elements<KeyValue<K, V>> getMembers() {
		return keys().map((key) -> KeyValue.of(key, creator.apply(key)));
	}

	@Override
	public Iterator<KeyValue<K, V>> iterator() {
		return getMembers().iterator();
	}

	@Override
	public Elements<K> keys() {
		return keysSupplier.get();
	}

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return getMembers().stream();
	}
}
