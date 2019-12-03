package scw.orm.sql;

import scw.orm.Mapper;
import scw.orm.MappingContext;
import scw.orm.sql.enums.CasType;

public interface SqlMapper extends Mapper, TableNameMapping {
	boolean isTable(Class<?> clazz);

	boolean isNullAble(MappingContext context);

	boolean isAutoIncrement(MappingContext context);

	String getCharsetName(MappingContext context);

	boolean isUnique(MappingContext context);

	CasType getCasType(MappingContext context);
}
