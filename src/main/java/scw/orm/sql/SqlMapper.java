package scw.orm.sql;

import java.util.Collection;
import java.util.Map;

import scw.orm.MappingContext;
import scw.orm.Mapper;
import scw.orm.sql.enums.CasType;

public interface SqlMapper extends Mapper, TableNameMapping {
	Collection<MappingContext> getPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getPrimaryKeys(Class<?> clazz);

	Collection<MappingContext> getNotPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getNotPrimaryKeys(Class<?> clazz);

	Collection<MappingContext> getSqlMappingContext(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getSqlMappingContext(Class<?> clazz);

	TableMappingContext getTableMappingContext(MappingContext supperContext, Class<?> tableClass, boolean useFieldName);

	TableMappingContext getTableMappingContext(Class<?> tableClass);

	boolean isDataBaseMappingContext(MappingContext mappingContext);

	boolean isPrimaryKey(MappingContext mappingContext);

	<T> String getObjectKey(Class<? extends T> clazz, T bean);

	String getObjectKeyById(Class<?> clazz, Collection<Object> ids);

	boolean isTable(Class<?> clazz);

	boolean isNullAble(MappingContext context);

	<K> Map<String, K> getInIdKeyMap(Class<?> clazz, Collection<K> inIds, Object[] params);

	boolean isIgnore(MappingContext context);

	boolean isAutoIncrement(MappingContext context);

	String getCharsetName(MappingContext context);

	boolean isUnique(MappingContext context);

	CasType getCasType(MappingContext context);
}
