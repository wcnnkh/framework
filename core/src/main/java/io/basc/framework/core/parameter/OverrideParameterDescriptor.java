package io.basc.framework.core.parameter;

import io.basc.framework.lang.Nullable;
import io.basc.framework.value.Value;

public class OverrideParameterDescriptor extends ParameterDescriptorWrapper<ParameterDescriptor> {
	private final String name;
	private final Value defaultValue;

	public OverrideParameterDescriptor(ParameterDescriptor parameterDescriptor, @Nullable String name) {
		this(parameterDescriptor, name, null);
	}

	public OverrideParameterDescriptor(ParameterDescriptor parameterDescriptor, @Nullable Value defaultValue) {
		this(parameterDescriptor, null, defaultValue);
	}

	public OverrideParameterDescriptor(ParameterDescriptor parameterDescriptor, @Nullable String name,
			@Nullable Value defaultValue) {
		super(parameterDescriptor);
		this.name = name;
		this.defaultValue = defaultValue;
	}

	@Override
	public ParameterDescriptor rename(String name) {
		return new OverrideParameterDescriptor(getDelegateSource(), name);
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
