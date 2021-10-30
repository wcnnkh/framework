package io.basc.framework.orm;

import io.basc.framework.util.Wrapper;

public class PropertyMetadataWrapper<W extends PropertyMetadata> extends Wrapper<W> implements PropertyMetadata {

	public PropertyMetadataWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public String getName() {
		return wrappedTarget.getName();
	}

	@Override
	public boolean isAutoIncrement() {
		return wrappedTarget.isAutoIncrement();
	}

	@Override
	public boolean isPrimaryKey() {
		return wrappedTarget.isPrimaryKey();
	}

	@Override
	public boolean isNullable() {
		return wrappedTarget.isNullable();
	}

	@Override
	public String getCharsetName() {
		return wrappedTarget.getCharsetName();
	}

	@Override
	public String getComment() {
		return wrappedTarget.getComment();
	}

	@Override
	public boolean isUnique() {
		return wrappedTarget.isUnique();
	}

}
