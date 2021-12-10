package io.basc.framework.mapper;

import io.basc.framework.util.page.Pageables;

public interface FieldMetadatas extends Pageables<Class<?>, FieldMetadata> {

	@Override
	default FieldMetadatas next() {
		return jumpTo(getNextCursorId());
	}

	@Override
	FieldMetadatas jumpTo(Class<?> cursorId);

	@Override
	default boolean hasNext() {
		Class<?> next = getNextCursorId();
		return next != null && next != Object.class;
	}

	@Override
	default Class<?> getNextCursorId() {
		return getCursorId().getSuperclass();
	}
}
