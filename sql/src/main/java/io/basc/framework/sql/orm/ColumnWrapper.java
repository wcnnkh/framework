package io.basc.framework.sql.orm;

import io.basc.framework.mapper.Field;

public class ColumnWrapper<T extends Column> extends ColumnDescriptorWrapper<T> implements Column{

	public ColumnWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Field getField() {
		return wrappedTarget.getField();
	}
}
