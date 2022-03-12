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

	public boolean isEmpty() {
		return fieldDescriptor == null;
	}

	@Override
	public boolean isAutoIncrement() {
		if(fieldDescriptor == null) {
			return false;
		}
		
		return getObjectRelationalResolver().isAutoIncrement(entityClass, fieldDescriptor);
	}

	@Override
	public String getName() {
		if(fieldDescriptor == null) {
			return null;
		}
		return objectRelationalResolver.getName(entityClass, fieldDescriptor);
	}

	@Override
	public boolean isPrimaryKey() {
		if(fieldDescriptor == null) {
			return false;
		}
		return objectRelationalResolver.isPrimaryKey(entityClass, fieldDescriptor);
	}

	@Override
	public boolean isNullable() {
		if(fieldDescriptor == null) {
			return true;
		}
		return objectRelationalResolver.isNullable(entityClass, fieldDescriptor);
	}

	@Override
	public String getCharsetName() {
		if(fieldDescriptor == null) {
			return null;
		}
		return objectRelationalResolver.getCharsetName(entityClass, fieldDescriptor);
	}

	@Override
	public String getComment() {
		if(fieldDescriptor == null) {
			return null;
		}
		return objectRelationalResolver.getComment(entityClass, fieldDescriptor);
	}

	@Override
	public boolean isUnique() {
		if(fieldDescriptor == null) {
			return false;
		}
		return objectRelationalResolver.isUnique(entityClass, fieldDescriptor);
	}

}
