package io.basc.framework.core.execution.stereotype;

import io.basc.framework.core.convert.transform.PropertyMapping;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class PropertyTemplateMapping<T extends PropertyAccessDescriptor> implements PropertyMapping<PropertyAccess<T>> {
	@NonNull
	private final PropertyAccessTemplate<? extends T> template;
	private final Object target;

	@Override
	public Elements<PropertyAccess<T>> getElements() {
		return template.getElements().map((e) -> new PropertyAccess<>(e, target));
	}
}
