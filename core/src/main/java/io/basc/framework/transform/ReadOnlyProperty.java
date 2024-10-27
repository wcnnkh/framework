package io.basc.framework.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.lang.ObjectValue;
import io.basc.framework.execution.param.Arg;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class ReadOnlyProperty implements Property {
	@NonNull
	private final Parameter parameter;

	public ReadOnlyProperty(int positionIndex, ObjectValue value) {
		this(new Arg(positionIndex, value));
	}

	public ReadOnlyProperty(String name, ObjectValue value) {
		this(new Arg(name, value));
	}

	@Override
	public Elements<String> getAliasNames() {
		return parameter.getAliasNames();
	}

	@Override
	public String getName() {
		return parameter.getName();
	}

	@Override
	public int getPositionIndex() {
		return parameter.getPositionIndex();
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return parameter.getTypeDescriptor();
	}

	@Override
	public Object getValue() {
		return parameter.getValue();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException(parameter.getName());
	}
}
