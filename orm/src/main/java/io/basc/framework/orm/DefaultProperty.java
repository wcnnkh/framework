package io.basc.framework.orm;

import io.basc.framework.mapper.Field;

public class DefaultProperty implements Property {
	private final Class<?> entityClass;
	private final Field field;
	private final ObjectRelationalResolver objectRelationalResolver;

	public DefaultProperty(Class<?> entityClass, Field field, ObjectRelationalResolver objectRelationalResolver) {
		this.entityClass = entityClass;
		this.field = field;
		this.objectRelationalResolver = objectRelationalResolver;
	}

	@Override
	public String getName() {
		return objectRelationalResolver.getName(entityClass, field.getGetter());
	}

	@Override
	public boolean isPrimaryKey() {
		return objectRelationalResolver.isPrimaryKey(entityClass, field.getGetter());
	}

	@Override
	public boolean isNullable() {
		if(!field.isSupportGetter()) {
			
		}
		
		return objectRelationalResolver.isNullable(entityClass, field.getGetter());
	}

	@Override
	public Field getField() {
		return field;
	}
}
