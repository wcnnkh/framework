package io.basc.framework.orm;

import io.basc.framework.mapper.FieldDescriptor;

public class DefaultPropertyMetadata implements PropertyMetadata {
	private final FieldDescriptor fieldDescriptor;
	private final ObjectRelationalResolver objectRelationalResolver;
	private final Class<?> entityClass;

	public DefaultPropertyMetadata(ObjectRelationalResolver objectRelationalResolver, Class<?> entityClass,
			FieldDescriptor fieldDescriptor) {
		this.entityClass = entityClass;
		this.objectRelationalResolver = objectRelationalResolver;
		this.fieldDescriptor = fieldDescriptor;
	}

	public FieldDescriptor getFieldDescriptor() {
		return fieldDescriptor;
	}

	public ObjectRelationalResolver getObjectRelationalResolver() {
		return objectRelationalResolver;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	@Override
	public boolean isAutoIncrement() {
		return getObjectRelationalResolver().isAutoIncrement(entityClass, fieldDescriptor);
	}

	@Override
	public String getName() {
		return objectRelationalResolver.getName(entityClass, fieldDescriptor);
	}

	@Override
	public boolean isPrimaryKey() {
		return objectRelationalResolver.isPrimaryKey(entityClass, fieldDescriptor);
	}

	@Override
	public boolean isNullable() {
		return objectRelationalResolver.isNullable(entityClass, fieldDescriptor);
	}

	@Override
	public String getCharsetName() {
		return objectRelationalResolver.getCharsetName(entityClass, fieldDescriptor);
	}

	@Override
	public String getComment() {
		return objectRelationalResolver.getComment(entityClass, fieldDescriptor);
	}

	@Override
	public boolean isUnique() {
		return objectRelationalResolver.isUnique(entityClass, fieldDescriptor);
	}

}
