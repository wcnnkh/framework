package io.basc.framework.transform.collection;

import java.util.List;
import java.util.stream.IntStream;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
@SuppressWarnings("rawtypes")
public class ListProperties implements Properties {
	@NonNull
	private final List list;
	@NonNull
	private final TypeDescriptor typeDescriptor;

	public ListProperties(List list) {
		this(list, TypeDescriptor.forObject(list));
	}

	@Override
	public Elements<Property> getElements() {
		return Elements.of(() -> IntStream.range(0, list.size())
				.mapToObj((index) -> new ListProperty(list, typeDescriptor, index)));
	}

	@Override
	public Property getElement(int index) {
		return new ListProperty(list, typeDescriptor, index);
	}
}
