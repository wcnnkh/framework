package io.basc.framework.orm;

/**
 * 对象映射关系
 * 
 * @author shuchaowen
 *
 */
public interface ObjectRelationalMapper extends ObjectRelationalResolver {

	default ObjectRelational<? extends Property> getStructure(Class<?> entityClass) {
		return new EntityStructure(entityClass, this, null).withSuperclass().clone();
	}
}
