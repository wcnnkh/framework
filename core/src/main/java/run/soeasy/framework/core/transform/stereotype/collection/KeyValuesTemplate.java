package run.soeasy.framework.core.transform.stereotype.collection;

import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.transform.stereotype.Accessor;
import run.soeasy.framework.core.transform.stereotype.Template;
import run.soeasy.framework.util.KeyValue;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.collections.KeyValues;
import run.soeasy.framework.util.collections.Elements.ElementsWrapper;

@RequiredArgsConstructor
@Getter
public class KeyValuesTemplate<K, V extends Accessor>
		implements Template<K, V>, KeyValues<K, V>, ElementsWrapper<KeyValue<K, V>, Elements<KeyValue<K, V>>> {
	@NonNull
	private final Supplier<? extends Elements<K>> keysSupplier;
	@NonNull
	private final Function<? super K, ? extends V> creator;

	@Override
	public Elements<V> getAccessors(K key) {
		V access = creator.apply(key);
		return Elements.singleton(access);
	}

	@Override
	public Elements<K> getAccessorIndexes() {
		return keys();
	}

	@Override
	public Elements<KeyValue<K, V>> getSource() {
		return keys().map((key) -> KeyValue.of(key, creator.apply(key)));
	}

	@Override
	public Elements<K> keys() {
		return keysSupplier.get();
	}
}
