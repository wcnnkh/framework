package scw.orm.sql;

import scw.orm.AbstractMapper;
import scw.orm.FieldColumn;
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

	public boolean isIndexColumn(MappingContext context) {
		return context.getColumn().getAnnotation(Index.class) != null;
	}

	public boolean isNullable(MappingContext context) {
		if (!super.isNullable(context)) {
			return false;
		}

		if (isPrimaryKey(context) || isIndexColumn(context)) {
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
		return isTable(context.getColumn().getType()) || super.isEntity(context);
	}

	@Override
	public boolean isIgnore(MappingContext context) {
		if (!(context.getColumn() instanceof FieldColumn)) {
			return true;
		}

		if (((FieldColumn) context.getColumn()).getField() == null) {
			return true;
		}
		return super.isIgnore(context);
	}
}
