package io.basc.framework.mapper.io.table;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.Parameter;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Item;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ParameterColumn implements Column {
	private final Parameter parameter;
	private final int positionIndex;
	private final Item row;

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
		return positionIndex;
	}

	@Override
	public Item getRow() {
		return row;
	}

	@Override
	public TypeDescriptor getTypeDescriptor() {
		return parameter.getTypeDescriptor();
	}

	@Override
	public Object getValue() {
		return parameter.getValue();
	}
}
