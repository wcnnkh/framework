package io.basc.framework.mapper.stereotype;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.transform.Property;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AbstractField implements Property {
	private final FieldDescriptor fieldDescriptor;

	@Override
	public String getName() {
		return fieldDescriptor.getName();
	}

	@Override
	public Elements<String> getAliasNames() {
		return fieldDescriptor.getAliasNames();
	}

	@Override
	public int getPositionIndex() {
		return fieldDescriptor.getPositionIndex();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return fieldDescriptor.getter().getTypeDescriptor();
	}

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		return fieldDescriptor.setter().getTypeDescriptor();
	}

	@Override
	public boolean isReadOnly() {
		return !fieldDescriptor.isSupportSetter();
	}
}
