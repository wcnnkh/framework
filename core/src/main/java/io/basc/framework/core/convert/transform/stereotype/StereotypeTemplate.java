package io.basc.framework.core.convert.transform.stereotype;

import io.basc.framework.core.convert.transform.PropertyTemplate;
import io.basc.framework.util.collections.Elements;
import lombok.Data;
import lombok.NonNull;

@Data
public class StereotypeTemplate<S extends StereotypeDescriptor, SM extends StereotypeMapping<? extends S>>
		implements PropertyTemplate<StereotypeProperty<S>>, StereotypeMapping<StereotypeProperty<S>> {
	@NonNull
	private final SM source;
	private final Object target;

	@Override
	public Elements<StereotypeProperty<S>> getElements() {
		return source.getElements().map((e) -> new StereotypeProperty<>(e, target));
	}

	@Override
	public String getName() {
		return source.getName();
	}
}
