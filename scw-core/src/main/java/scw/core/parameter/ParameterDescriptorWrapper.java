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
		return wrappedTarget.getName();
	}

	public Class<?> getType() {
		return wrappedTarget.getType();
	}

	public Type getGenericType() {
		return wrappedTarget.getGenericType();
	}

	public boolean isNullable() {
		return wrappedTarget.isNullable();
	}

	public Value getDefaultValue() {
		return wrappedTarget.getDefaultValue();
	}

	@Override
	public String getDescription() {
		return wrappedTarget.getDescription();
	}
}
