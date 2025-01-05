package io.basc.framework.core.mapping.stereotype;

import io.basc.framework.core.mapping.PropertyTemplate;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class MappingTemplate<T extends FieldDescriptor, W extends MappingDescriptor<T>>
		implements PropertyTemplate<Field<T>>, MappingDescriptor<Field<T>> {
	@NonNull
	private final W source;
	private final Object target;

	@Override
	public Elements<Field<T>> getElements() {
		return source.getElements().map((e) -> new Field<>(e, target));
	}

	@Override
	public String getName() {
		return source.getName();
	}
}
