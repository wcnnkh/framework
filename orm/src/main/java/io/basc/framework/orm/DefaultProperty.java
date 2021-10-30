package io.basc.framework.orm;

import java.util.Collection;

import io.basc.framework.data.domain.Range;
import io.basc.framework.mapper.Field;

public class DefaultProperty extends DefaultPropertyMetadata implements Property {
	private final Field field;

	public DefaultProperty(ObjectRelationalResolver objectRelationalResolver, Class<?> entityClass, Field field) {
		super(objectRelationalResolver, entityClass, field.getGetter());
		this.field = field;
	}

	@Override
	public Collection<String> getAliasNames() {
		return getObjectRelationalResolver().getAliasNames(getEntityClass(), field.getSetter());
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public Collection<Range<Double>> getNumberRanges() {
		return getObjectRelationalResolver().getNumberRanges(getEntityClass(), field.getSetter());
	}

	@Override
	public boolean isVersion() {
		return getObjectRelationalResolver().isVersionField(getEntityClass(), getFieldDescriptor());
	}

	@Override
	public boolean isIncrement() {
		return getObjectRelationalResolver().isIncrement(getEntityClass(), getFieldDescriptor());
	}

	@Override
	public boolean isEntity() {
		return getObjectRelationalResolver().isEntity(getEntityClass(), getFieldDescriptor());
	}
}
