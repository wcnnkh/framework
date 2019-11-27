package scw.orm.sql;

import java.util.Collection;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;

public interface SqlMappingOperations extends MappingOperations, TableNameMapping {
	Collection<MappingContext> getPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getPrimaryKeys(Class<?> clazz);

	Collection<MappingContext> getNotPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getNotPrimaryKeys(Class<?> clazz);

	Collection<MappingContext> getSqlMappingContext(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getSqlMappingContext(Class<?> clazz);

	TableMappingContext getTableMappingContext(MappingContext supperContext, Class<?> tableClass, boolean useFieldName);

	TableMappingContext getTableMappingContext(Class<?> tableClass);

	boolean isDataBaseMappingContext(MappingContext mappingContext);

	boolean isPrmaryKey(MappingContext mappingContext);

	<T> String getObjectKey(Class<? extends T> clazz, T bean);

	String getObjectKeyById(Class<?> clazz, Collection<Object> ids);
	
	boolean isTable(Class<?> clazz);
	
	boolean isNullAble(MappingContext context);
}
