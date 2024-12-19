package io.basc.framework.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.transform.Parameter;
import io.basc.framework.core.execution.param.Arg;
import io.basc.framework.util.Elements;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class ReadOnlyProperty implements Property {
	@NonNull
	private final Parameter parameter;

	public ReadOnlyProperty(int positionIndex, Value value) {
		this(new Arg(positionIndex, value));
	}

	public ReadOnlyProperty(String name, Value value) {
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
