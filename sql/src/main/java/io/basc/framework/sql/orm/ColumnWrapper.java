package io.basc.framework.sql.orm;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;

public class ColumnWrapper<T extends Column> extends ColumnMetadataWrapper<T> implements Column {

	public ColumnWrapper(T wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public Field getField() {
		return wrappedTarget.getField();
	}

	@Override
	public Collection<String> getAliasNames() {
		return wrappedTarget.getAliasNames();
	}

	@Override
	public Collection<Range<Double>> getNumberRanges() {
		return wrappedTarget.getNumberRanges();
	}

	@Override
	public boolean isVersion() {
		return wrappedTarget.isVersion();
	}

	@Override
	public boolean isIncrement() {
		return wrappedTarget.isIncrement();
	}
}
