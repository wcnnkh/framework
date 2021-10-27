package io.basc.framework.orm;

import java.util.stream.Stream;

import io.basc.framework.mapper.Field;
import io.basc.framework.mapper.FieldFactory;
import io.basc.framework.mapper.Fields;

/**
 * 对象映射关系
 * 
 * @author shuchaowen
 *
 */
public interface ObjectRelationalMapping extends ObjectRelationalResolver, FieldFactory {
	
	Property resolve(Class<?> entityClass, Field field);
	
	default Stream<? extends Property> resolve(Class<?> entityClass, Fields fields){
		return fields.stream().map((field) -> resolve(entityClass, field));
	}
	
	EntityStructure<? extends Property> resolve(Class<?> entityClass);
}
