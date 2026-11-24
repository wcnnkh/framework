package run.soeasy.framework.core.mapping.property;

import run.soeasy.framework.core.convert.value.TypedValueAccessorWrapper;

public interface PropertyAccessorWrapper<W extends PropertyAccessor> extends PropertyAccessor, PropertyDescriptorWrapper<W>, TypedValueAccessorWrapper<W> {
	@Override
	default PropertyAccessor rename(String name) {
		return getSource().rename(name);
	}
}
