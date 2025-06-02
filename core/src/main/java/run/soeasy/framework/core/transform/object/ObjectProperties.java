package run.soeasy.framework.core.transform.object;

import lombok.NonNull;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.transform.property.PropertyTemplate;
import run.soeasy.framework.core.transform.property.PropertyTemplateProperties;
import run.soeasy.framework.core.transform.property.TypedProperties;

public class ObjectProperties<S extends Property, T extends PropertyTemplate<S>>
		extends PropertyTemplateProperties<S, T, PropertyAccessor> implements TypedProperties {

	public ObjectProperties(@NonNull T template, Object target) {
		super(template, (e) -> new ObjectPropertyAccessor<>(e, target));
	}
}
