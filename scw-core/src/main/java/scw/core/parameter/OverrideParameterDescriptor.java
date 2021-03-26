package scw.core.parameter;

import scw.lang.Nullable;
import scw.value.Value;

public class OverrideParameterDescriptor extends ParameterDescriptorWrapper {
	private final String name;
	private final Value defaultValue;

	public OverrideParameterDescriptor(ParameterDescriptor parameterDescriptor,
			@Nullable String name) {
		this(parameterDescriptor, name, null);
	}

	public OverrideParameterDescriptor(ParameterDescriptor parameterDescriptor,
			@Nullable Value defaultValue) {
		this(parameterDescriptor, null, defaultValue);
	}

	public OverrideParameterDescriptor(ParameterDescriptor parameterDescriptor,
			@Nullable String name, @Nullable Value defaultValue) {
		super(parameterDescriptor);
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	public Value getDefaultValue() {
		return defaultValue == null ? super.getDefaultValue() : defaultValue;
	}
}
