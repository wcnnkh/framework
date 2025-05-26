package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptor.AccessibleDescriptorWrapper;

@FunctionalInterface
public interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
		extends PropertyDescriptor, AccessibleDescriptorWrapper<W> {
	@Override
	default String getName() {
		return getSource().getName();
	}

	@Override
	default PropertyDescriptor rename(String name) {
		return getSource().rename(name);
	}
}
