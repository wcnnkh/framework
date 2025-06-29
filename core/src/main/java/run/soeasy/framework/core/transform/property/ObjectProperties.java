package run.soeasy.framework.core.transform.property;

import lombok.NonNull;

public class ObjectProperties<S extends Property, T extends PropertyTemplate<S>>
		extends PropertyTemplateProperties<S, T, PropertyAccessor> implements TypedProperties {

	public ObjectProperties(@NonNull T template, Object target) {
		super(template, (e) -> new ObjectPropertyAccessor<>(e, target));
	}
}
