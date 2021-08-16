package scw.orm.sql;

import scw.mapper.Field;

public class ColumnWrapper<T extends Column> extends ColumnDescriptorWrapper<T> implements Column{

	public ColumnWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Field getField() {
		return wrappedTarget.getField();
	}
}
