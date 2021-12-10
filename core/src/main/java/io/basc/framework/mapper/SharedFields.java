package io.basc.framework.mapper;

import java.util.Collections;
import java.util.List;

public class SharedFields extends AbstractFields implements Fields {
	private final List<Field> sharedFields;

	public SharedFields(Class<?> cursorId, Fields fields, List<Field> sharedFields) {
		super(cursorId, fields);
		this.sharedFields = sharedFields;
	}

	public SharedFields(Class<?> cursorId, Class<?> nextCursorId, Fields fields, List<Field> sharedFields) {
		super(cursorId, nextCursorId, fields);
		this.sharedFields = sharedFields;
	}

	@Override
	public Fields shared() {
		return this;
	}

	@Override
	public List<Field> getList() {
		return Collections.unmodifiableList(sharedFields);
	}
}
