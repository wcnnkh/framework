package io.basc.framework.orm;

import io.basc.framework.mapper.StructureFactory;

/**
 * 对象映射关系
 * 
 * @author shuchaowen
 *
 */
public interface ObjectRelationalFactory extends ObjectRelationalResolver, StructureFactory {

	@Override
	default Boolean isEntity(Class<?> entityClass) {
		return StructureFactory.super.isEntity(entityClass);
	}

	default ObjectRelational<? extends Property> getStructure(Class<?> entityClass) {
		return new EntityStructure(entityClass, this, null).withSuperclass().clone();
	}
}
