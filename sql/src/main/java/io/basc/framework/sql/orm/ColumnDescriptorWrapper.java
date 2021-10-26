package io.basc.framework.sql.orm;

import io.basc.framework.orm.PropertyDescriptorWrapper;

public class ColumnDescriptorWrapper<T extends ColumnDescriptor> extends PropertyDescriptorWrapper<T> implements ColumnDescriptor{

	public ColumnDescriptorWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public boolean isAutoIncrement() {
		return wrappedTarget.isAutoIncrement();
	}

	@Override
	public boolean isUnique() {
		return wrappedTarget.isUnique();
	}

	@Override
	public String getCharsetName() {
		return wrappedTarget.getCharsetName();
	}

	@Override
	public String getComment() {
		return wrappedTarget.getComment();
	}

}
