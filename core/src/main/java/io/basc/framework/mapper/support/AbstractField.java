package io.basc.framework.mapper.support;

import io.basc.framework.mapper.Field;
import io.basc.framework.util.ObjectUtils;

public abstract class AbstractField extends AbstractParameterDescriptor implements Field {

	@Override
	public int hashCode() {
		return getName().hashCode() + getTypeDescriptor().getType().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AbstractField) {
			AbstractField field = (AbstractField) obj;
			return ObjectUtils.equals(getTypeDescriptor().getType(), field.getTypeDescriptor().getType())
					&& getName().equals(field.getName());
		}
		return false;
	}
}
