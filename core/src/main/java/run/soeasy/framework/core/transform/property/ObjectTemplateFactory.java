package run.soeasy.framework.core.transform.property;

public interface ObjectTemplateFactory<E extends Property> {
	default boolean hasObjectTemplate(Class<?> objectClass) {
		return getObjectTemplate(objectClass) != null;
	}

	PropertyTemplate<E> getObjectTemplate(Class<?> objectClass);
}
