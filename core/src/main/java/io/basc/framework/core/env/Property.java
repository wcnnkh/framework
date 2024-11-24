package io.basc.framework.core.env;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.transform.Access;

public interface Property extends Access, PropertyDescriptor {

	@Override
	default TypeDescriptor getTypeDescriptor() {
		return Access.super.getTypeDescriptor();
	}
}
