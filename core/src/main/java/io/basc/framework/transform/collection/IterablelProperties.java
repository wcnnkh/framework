package io.basc.framework.transform.collection;

import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.ReadOnlyProperty;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class IterablelProperties implements Properties {
	@NonNull
	private final Iterable<?> collection;

	@Override
	public Elements<Property> getElements() {
		return Elements.of(collection).index()
				.map((indexed) -> new ReadOnlyProperty((int) indexed.getIndex(), ObjectValue.of(indexed.getElement())));
	}
}
