package run.soeasy.framework.core.transform.mapping;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.transform.stereotype.PropertyTemplate;
import run.soeasy.framework.util.collection.Elements;

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
