package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.convert.value.AccessibleDescriptor;
import run.soeasy.framework.core.convert.value.AccessibleDescriptor.AccessibleDescriptorWrapper;
import run.soeasy.framework.core.domain.Wrapped;

public class NamedAccessibleDescriptor<W extends AccessibleDescriptor> extends Wrapped<W>
		implements PropertyDescriptor, AccessibleDescriptorWrapper<W> {
	private final String name;

	public NamedAccessibleDescriptor(W source, String name) {
		super(source);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public PropertyDescriptor rename(String name) {
		return new NamedAccessibleDescriptor<>(getSource(), name);
	}
}
