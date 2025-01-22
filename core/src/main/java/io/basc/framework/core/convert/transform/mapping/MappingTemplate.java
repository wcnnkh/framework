package io.basc.framework.core.convert.transform.mapping;

import io.basc.framework.core.convert.transform.stereotype.PropertyTemplate;
import io.basc.framework.util.collections.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class MappingTemplate<S extends FieldDescriptor, SM extends MappingDescriptor<? extends S>>
		implements PropertyTemplate<Field<S>>, MappingDescriptor<Field<S>> {
	@NonNull
	private final SM source;
	private final Object target;

	@Override
	public Elements<Field<S>> getElements() {
		return source.getElements().map((e) -> new Field<>(e, target));
	}

	@Override
	public String getName() {
		return source.getName();
	}
}
