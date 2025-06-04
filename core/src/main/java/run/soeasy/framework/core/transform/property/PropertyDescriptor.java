package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptor;

public interface PropertyDescriptor extends AccessibleDescriptor {
	public static final PropertyDescriptor[] EMPTY_ARRAY = new PropertyDescriptor[0];

	String getName();

	default PropertyDescriptor rename(String name) {
		return new NamedAccessibleDescriptor<>(this, name);
	}
}
