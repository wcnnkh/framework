package run.soeasy.framework.core.transform.object;

import run.soeasy.framework.core.transform.property.PropertyTemplate;

public interface ObjectTemplateFactory<E extends Property> {
	default boolean hasObjectTemplate(Class<?> objectClass) {
		return getObjectTemplate(objectClass) != null;
	}

	PropertyTemplate<E> getObjectTemplate(Class<?> objectClass);
}
