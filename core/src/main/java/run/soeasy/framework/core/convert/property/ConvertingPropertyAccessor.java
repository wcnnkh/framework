package run.soeasy.framework.core.convert.property;

import lombok.NonNull;
import run.soeasy.framework.core.convert.ConvertingValue;
import run.soeasy.framework.core.convert.property.PropertyDescriptor.PropertyDescriptorWrapper;

public class ConvertingPropertyAccessor<W extends PropertyDescriptor> extends ConvertingValue<W>
		implements PropertyAccessor, PropertyDescriptorWrapper<W> {
	private static final long serialVersionUID = 1L;

	public ConvertingPropertyAccessor(@NonNull W source) {
		super(source);
	}

	@Override
	public PropertyAccessor rename(String name) {
		return PropertyAccessor.super.rename(name);
	}
}
