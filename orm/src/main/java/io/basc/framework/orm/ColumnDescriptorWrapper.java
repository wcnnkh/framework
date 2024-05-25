package io.basc.framework.orm;

import io.basc.framework.data.repository.IndexInfo;
import io.basc.framework.mapper.entity.FieldDescriptorWrapper;
import io.basc.framework.util.Range;
import io.basc.framework.util.element.Elements;

public class ColumnDescriptorWrapper<W extends ColumnDescriptor> extends FieldDescriptorWrapper<W>
		implements ColumnDescriptor {

	public ColumnDescriptorWrapper(W wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public boolean isAutoIncrement() {
		return wrappedTarget.isAutoIncrement();
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
	public boolean isIncrement() {
		return wrappedTarget.isIncrement();
	}

	@Override
	public Elements<? extends Range<Double>> getNumberRanges() {
		return wrappedTarget.getNumberRanges();
	}

	@Override
	public boolean isPrimaryKey() {
		return wrappedTarget.isPrimaryKey();
	}

	@Override
	public boolean isUnique() {
		return wrappedTarget.isUnique();
	}

	@Override
	public boolean isVersion() {
		return wrappedTarget.isVersion();
	}

	@Override
	public boolean isEntity() {
		return wrappedTarget.isEntity();
	}

	@Override
	public boolean isNullable() {
		return wrappedTarget.isNullable();
	}

	@Override
	public Elements<IndexInfo> getIndexs() {
		return wrappedTarget.getIndexs();
	}

	@Override
	public boolean hasIndex() {
		return wrappedTarget.hasIndex();
	}
}
