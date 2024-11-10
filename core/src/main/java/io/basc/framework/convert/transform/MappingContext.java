package io.basc.framework.convert.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ValueWrapper;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.Listable;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.attribute.SimpleAttributes;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class MappingContext<K, V extends Accessor, M extends Mapping<K, ? extends V>>
		extends SimpleAttributes<String, ValueWrapper>
		implements ParentDiscover<MappingContext<K, V, M>>, Listable<KeyValue<K, V>> {
	@NonNull
	private final M mapping;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final KeyValue<K, V> accesstor;
	private final MappingContext<K, V, M> parent;

	@Override
	public Elements<KeyValue<K, V>> getElements() {
		return Elements.singleton(accesstor).concat(parents().map((e) -> e.getAccesstor()));
	}
}
