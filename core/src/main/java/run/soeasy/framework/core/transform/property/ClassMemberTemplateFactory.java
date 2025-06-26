package run.soeasy.framework.core.transform.property;

import run.soeasy.framework.core.type.ClassMembersLoader;

public interface ClassMemberTemplateFactory<E extends Property> {
	default boolean hasClassPropertyTemplate(Class<?> requiredClass) {
		return getClassPropertyTemplate(requiredClass) != null;
	}

	ClassMembersLoader<E> getClassPropertyTemplate(Class<?> requriedClass);
}
