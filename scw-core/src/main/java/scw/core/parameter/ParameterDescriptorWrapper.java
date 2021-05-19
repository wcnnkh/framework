package scw.core.parameter;

import java.lang.reflect.Type;

import scw.core.annotation.AnnotatedElementWrapper;
import scw.value.Value;

public class ParameterDescriptorWrapper<P extends ParameterDescriptor> extends AnnotatedElementWrapper<P>
		implements ParameterDescriptor {

	public ParameterDescriptorWrapper(P target) {
		super(target);
	}

	public String getName() {
		return target.getName();
	}

	public Class<?> getType() {
		return target.getType();
	}

	public Type getGenericType() {
		return target.getGenericType();
	}

	public boolean isNullable() {
		return target.isNullable();
	}

	public Value getDefaultValue() {
		return target.getDefaultValue();
	}

	@Override
	public String getDescription() {
		return target.getDescription();
	}
}
