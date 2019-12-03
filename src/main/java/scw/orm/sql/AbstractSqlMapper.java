package scw.orm.sql;

import scw.core.reflect.FieldDefinition;
import scw.orm.AbstractMapper;
import scw.orm.MappingContext;
import scw.orm.sql.annotation.AutoIncrement;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.enums.CasType;

public abstract class AbstractSqlMapper extends AbstractMapper implements SqlMapper {
	public boolean isTable(Class<?> clazz) {
		return clazz.getAnnotation(Table.class) != null;
	}

	public boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(Index.class) != null;
	}

	public boolean isNullAble(MappingContext context) {
		if (context.getColumn().getField().getType().isPrimitive() || isPrimaryKey(context)
				|| isIndexColumn(context.getColumn())) {
			return false;
		}

		Column column = context.getColumn().getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}

	public boolean isAutoIncrement(MappingContext context) {
		return context.getColumn().getAnnotation(AutoIncrement.class) != null;
	}

	public String getCharsetName(MappingContext context) {
		Column column = context.getColumn().getAnnotation(Column.class);
		return column == null ? null : column.charsetName().trim();
	}

	public boolean isUnique(MappingContext context) {
		Column column = context.getColumn().getAnnotation(Column.class);
		return column == null ? false : column.unique();
	}

	public CasType getCasType(MappingContext context) {
		if (isPrimaryKey(context)) {
			return CasType.NOTHING;
		}

		Column column = context.getColumn().getAnnotation(Column.class);
		if (column == null) {
			return CasType.NOTHING;
		}
		return column.casType();
	}

	@Override
	public boolean isEntity(MappingContext context) {
		return context.getColumn().getDeclaringClass().getAnnotation(Table.class) != null || super.isEntity(context);
	}
}
