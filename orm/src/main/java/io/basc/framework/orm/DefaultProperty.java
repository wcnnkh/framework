package io.basc.framework.orm;

import java.util.Collection;
import java.util.Collections;

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
		if(!field.isSupportSetter()) {
			return Collections.emptyList();
		}
		return getObjectRelationalResolver().getAliasNames(getEntityClass(), field.getSetter());
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public Collection<Range<Double>> getNumberRanges() {
		if(!field.isSupportSetter()) {
			return Collections.emptyList();
		}
		return getObjectRelationalResolver().getNumberRanges(getEntityClass(), field.getSetter());
	}

	@Override
	public boolean isVersion() {
		if(isEmpty()) {
			return false;
		}
		return getObjectRelationalResolver().isVersionField(getEntityClass(), getFieldDescriptor());
	}

	@Override
	public boolean isIncrement() {
		if(isEmpty()) {
			return false;
		}
		return getObjectRelationalResolver().isIncrement(getEntityClass(), getFieldDescriptor());
	}

	@Override
	public boolean isEntity() {
		if(isEmpty()) {
			return false;
		}
		return getObjectRelationalResolver().isEntity(getEntityClass(), getFieldDescriptor());
	}
}
