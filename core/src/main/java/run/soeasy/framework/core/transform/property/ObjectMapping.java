package run.soeasy.framework.core.transform.property;

import lombok.NonNull;

public class ObjectMapping<S extends Property, T extends PropertyTemplate<S>>
		extends PropertyTemplateProperties<S, T, PropertyAccessor> implements TypedProperties {

	public ObjectMapping(@NonNull T template, Object target) {
		super(template, (e) -> new ObjectPropertyAccessor<>(e, target));
	}
}
