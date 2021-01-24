package scw.value.factory;

import scw.lang.Nullable;
import scw.value.Value;

public interface ValueFactory<K> {
	@Nullable
	Value getValue(K key);
}
