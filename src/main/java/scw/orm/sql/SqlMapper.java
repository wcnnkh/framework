package scw.orm.sql;

import java.util.Collection;

import scw.orm.Mapper;
import scw.orm.MappingContext;
import scw.orm.sql.enums.CasType;

public interface SqlMapper extends Mapper, TableNameMapping {
	Collection<MappingContext> getNotPrimaryKeys(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getNotPrimaryKeys(Class<?> clazz);

	Collection<MappingContext> getSqlMappingContext(MappingContext supperContext, Class<?> clazz);

	Collection<MappingContext> getSqlMappingContext(Class<?> clazz);

	TableMappingContext getTableMappingContext(MappingContext supperContext, Class<?> tableClass, boolean useFieldName);

	TableMappingContext getTableMappingContext(Class<?> tableClass);

	boolean isDataBaseMappingContext(MappingContext mappingContext);

	boolean isTable(Class<?> clazz);

	boolean isNullAble(MappingContext context);

	boolean isIgnore(MappingContext context);

	boolean isAutoIncrement(MappingContext context);

	String getCharsetName(MappingContext context);

	boolean isUnique(MappingContext context);

	CasType getCasType(MappingContext context);
}
