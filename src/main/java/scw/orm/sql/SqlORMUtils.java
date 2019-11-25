package scw.orm.sql;

import scw.core.reflect.FieldDefinition;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.PrimaryKey;

public final class SqlORMUtils {
	private SqlORMUtils() {
	};

	public static boolean isPrimaryKey(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(PrimaryKey.class) != null;
	}

	public static boolean isIndexColumn(FieldDefinition fieldDefinition) {
		return fieldDefinition.getAnnotation(Index.class) != null;
	}

	public static boolean isNullAble(FieldDefinition fieldDefinition) {
		if (fieldDefinition.getField().getType().isPrimitive() || isPrimaryKey(fieldDefinition)
				|| isIndexColumn(fieldDefinition)) {
			return false;
		}

		Column column = fieldDefinition.getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}
}
