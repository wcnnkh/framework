package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.transform.templates.ArrayTemplate;

public class ArrayPropertyTemplate<T extends PropertyDescriptor, W extends PropertyTemplate<T>>
		extends ArrayTemplate<T, W> implements PropertyTemplateWrapper<T, W> {

	public ArrayPropertyTemplate(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public PropertyTemplate<T> asArray(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asArray(uniqueness);
	}
}
