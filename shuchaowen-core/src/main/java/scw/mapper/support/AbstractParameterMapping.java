package scw.mapper.support;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.RenameParameterDescriptor;
import scw.core.utils.StringUtils;
import scw.mapper.Field;
import scw.mapper.FieldDescriptor;
import scw.mapper.Setter;

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
		String name = nestingName ? getNestingDisplayName(field) : getDisplayName(setter);
		if (!StringUtils.isEmpty(basePrefix)) {
			name = basePrefix + "." + name;
		}

		return getValue(new RenameParameterDescriptor(setter, name));
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
