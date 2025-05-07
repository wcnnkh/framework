package run.soeasy.framework.core.mapping;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.property.PropertySource;

@Data
public class MappingTemplate<S extends FieldDescriptor, SM extends MappingDescriptor<? extends S>>
		implements PropertySource<Field<S>>, MappingDescriptor<Field<S>> {
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
