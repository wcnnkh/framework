package io.basc.framework.mapper;

import io.basc.framework.util.StringUtils;

public abstract class AbstractParameterMapping extends AbstractMapping {
	private String basePrefix;
	private boolean nestingName;

	public AbstractParameterMapping(boolean nestingName, String basePrefix) {
		this.nestingName = nestingName;
		this.basePrefix = basePrefix;
	}

	@Override
	protected Object getValue(Field field) {
		Setter setter = field.getSetter();
		String name = nestingName ? getNestingDisplayName(field) : setter.getName();
		if (!StringUtils.isEmpty(basePrefix)) {
			name = basePrefix + "." + name;
		}

		return getValue(new OverrideParameterDescriptor(setter, name));
	}

	protected abstract Object getValue(ParameterDescriptor parameterDescriptor);

	@Override
	protected boolean isNesting(FieldDescriptor fieldDescriptor) {
		if (!nestingName) {
			return false;
		}

		return super.isNesting(fieldDescriptor);
	}
}
