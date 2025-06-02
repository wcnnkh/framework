package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.transform.templates.MapTemplate;

public class MapPropertyTemplate<T extends PropertyDescriptor, W extends PropertyTemplate<T>> extends MapTemplate<T, W>
		implements PropertyTemplateWrapper<T, W> {

	public MapPropertyTemplate(W source, boolean uniqueness) {
		super(source, uniqueness);
	}

	@Override
	public PropertyTemplate<T> asMap(boolean uniqueness) {
		return isUniqueness() == uniqueness ? this : getSource().asMap(uniqueness);
	}
}
