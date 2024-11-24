package io.basc.framework.transform.array;

import java.lang.reflect.Array;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.transform.Property;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ArrayProperty implements Property {
	@NonNull
	private final Object array;
	@NonNull
	private final TypeDescriptor arrayTypeDescriptor;
	private final int index;

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return arrayTypeDescriptor.getElementTypeDescriptor();
	}

	@Override
	public int getPositionIndex() {
		return index;
	}

	@Override
	public boolean isPresent() {
		return index < Array.getLength(array);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getValue() {
		return Array.get(array, index);
	}

	@Override
	public void setValue(Object value) throws UnsupportedOperationException {
		Array.set(array, index, value);
	}

}
