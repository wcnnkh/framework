package io.basc.framework.orm;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldDescriptor;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.Fields;

/**
 * 对象映射关系
 * 
 * @author shuchaowen
 *
 */
public interface ObjectRelationalMapping extends ObjectRelationalResolver, ObjectRelationalProcessor, FieldFactory {

	@Override
	default EntityMetadata resolveMetadata(Class<?> entityClass) {
		return new DefaultEntityMetadata(this, entityClass);
	}

	@Override
	default PropertyMetadata resolveMetadata(Class<?> entityClass, FieldDescriptor fieldDescriptor) {
		return new DefaultPropertyMetadata(this, entityClass, fieldDescriptor);
	}

	@Override
	default Property resolve(Class<?> entityClass, Field field) {
		return new DefaultProperty(this, entityClass, field);
	}

	default EntityStructure<? extends Property> getStructure(Class<?> entityClass, Fields fields) {
		return new DefaultEntityStructure(this, this, entityClass, fields);
	}

	default EntityStructure<? extends Property> getStructure(Class<?> entityClass) {
		return getStructure(entityClass, getFields(entityClass).all());
	}

	default EntityStructure<? extends Property> getStructure(Class<?> entityClass, Field parentField) {
		return getStructure(entityClass, getFields(entityClass, parentField).all());
	}
}
