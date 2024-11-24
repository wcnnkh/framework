package io.basc.framework.transform.collection;

import java.util.List;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.transform.Property;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class ListProperty implements Property {
	@NonNull
	private final List<?> list;
	@NonNull
	private final TypeDescriptor listTypeDescriptor;
	private final int index;

	@Override
	public int getPositionIndex() {
		return index;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return listTypeDescriptor.getElementTypeDescriptor();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Object getValue() {
		return list.get(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValue(Object value) throws UnsupportedOperationException {
		((List<Object>) list).set(index, value);
	}

}
