package run.soeasy.framework.core.transform.property;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class ObjectProperties<S extends Property, T extends PropertyTemplate<S>>
		extends PropertyTemplateProperties<S, T, ObjectPropertyAccessor<S>> {
	private final Object target;

	public ObjectProperties(@NonNull T template, Object target) {
		super(template, (e) -> new ObjectPropertyAccessor<>(e, target));
		this.target = target;
	}
}
