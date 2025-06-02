package run.soeasy.framework.core.transform.property;

import lombok.NonNull;
import run.soeasy.framework.core.transform.templates.MapTemplate;

public class MapPropertyTemplate<T extends PropertyDescriptor, W extends PropertyTemplate<T>> extends MapTemplate<T, W>
		implements PropertyTemplateWrapper<T, W> {

	public MapPropertyTemplate(@NonNull W source) {
		super(source);
	}
	
	@Override
	public PropertyTemplate<T> asMap() {
		return this;
	}
	
	@Override
	public PropertyTemplate<T> asArray() {
		return getSource();
	}
}
