package io.basc.framework.transform.array;

import java.lang.reflect.Array;
import java.util.stream.IntStream;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ArrayProperties implements Properties {
	@NonNull
	private final Object array;
	@NonNull
	private final TypeDescriptor typeDescriptor;

	public ArrayProperties(Object array) {
		this(array, TypeDescriptor.forObject(array));
	}

	@Override
	public Elements<Property> getElements() {
		return Elements.of(() -> IntStream.range(0, Array.getLength(array))
				.mapToObj((index) -> new ArrayProperty(array, typeDescriptor, index)));
	}

	@Override
	public Property getElement(int index) {
		int len = Array.getLength(array);
		if (index >= len) {
			return null;
		}
		return new ArrayProperty(array, typeDescriptor, index);
	}
}
