package io.basc.framework.transform;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.execution.param.SimpleParameter;
import lombok.Setter;

@Setter
public class SimpleProperty extends SimpleParameter implements Property {
	private TypeDescriptor requiredTypeDescriptor;

	@Override
	public TypeDescriptor getRequiredTypeDescriptor() {
		return requiredTypeDescriptor == null ? Property.super.getRequiredTypeDescriptor() : requiredTypeDescriptor;
	}
}
