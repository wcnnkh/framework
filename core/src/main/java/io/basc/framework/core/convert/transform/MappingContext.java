package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.ValueWrapper;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Listable;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.attribute.SimpleAttributes;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MappingContext<K, V extends Access, M extends Mapping<K, V>> extends SimpleAttributes<String, ValueWrapper>
		implements ParentDiscover<MappingContext<K, V, M>>, Listable<KeyValue<K, V>> {
	@NonNull
	private final M mapping;
	@NonNull
	private final KeyValue<K, V> entry;
	@NonNull
	private final MappingContext<K, V, M> parent;

	/**
	 * 当前entry到最上层的映射
	 */
	@Override
	public Elements<KeyValue<K, V>> getElements() {
		return Elements.singleton(entry).concat(parents().map((e) -> e.getEntry()));
	}
}
