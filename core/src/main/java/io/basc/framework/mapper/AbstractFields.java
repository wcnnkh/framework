package io.basc.framework.mapper;

import io.basc.framework.util.StaticSupplier;

import java.util.function.Supplier;

public abstract class AbstractFields implements Fields {
	private final Class<?> cursorId;
	private final Fields fields;
	private Supplier<Class<?>> nextCursorId;

	public AbstractFields(Class<?> cursorId, Fields fields) {
		this.cursorId = cursorId;
		this.fields = fields;
	}

	public AbstractFields(Class<?> cursorId, Class<?> nextCursorId, Fields fields) {
		this(nextCursorId, fields);
		this.nextCursorId = new StaticSupplier<Class<?>>(nextCursorId);
	}

	@Override
	public Class<?> getNextCursorId() {
		return nextCursorId == null ? Fields.super.getNextCursorId() : nextCursorId.get();
	}

	@Override
	public Class<?> getCursorId() {
		return cursorId;
	}

	@Override
	public Fields jumpTo(Class<?> cursorId) {
		return fields.jumpTo(cursorId);
	}
}
