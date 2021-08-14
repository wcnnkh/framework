package scw.mapper;

import scw.util.page.Pageables;

public interface FieldMetadatas extends Pageables<Class<?>, FieldMetadata> {
	
	/**
	 * 数量
	 */
	@Override
	default long getCount() {
		return rows().size();
	}
	
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
