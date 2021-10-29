package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;

public class PropertyWrapper<W extends Property> extends PropertyMetadataWrapper<W> implements Property {

	public PropertyWrapper(W wrappedTarget) {
		super(wrappedTarget);
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
	public Field getField() {
		return wrappedTarget.getField();
	}

	@Override
	public boolean isVersion() {
		return wrappedTarget.isVersion();
	}

	@Override
	public boolean isIncrement() {
		return wrappedTarget.isIncrement();
	}

	@Override
	public boolean isEntity() {
		return wrappedTarget.isEntity();
	}
}
