package io.basc.framework.mapper;

import io.basc.framework.util.ObjectUtils;

public abstract class AbstractSetter extends AbstractParameterDescriptor implements Setter {
	@Override
	public int hashCode() {
		return getName().hashCode() + getTypeDescriptor().getType().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof Setter) {
			Setter setter = (Setter) obj;
			return ObjectUtils.equals(getTypeDescriptor().getType(), setter.getTypeDescriptor().getType())
					&& getName().equals(setter.getName());
		}
		return false;
	}

	@Override
	public String toString() {
		return "set(" + getTypeDescriptor().getResolvableType() + " " + getName() + ")";
	}
}
