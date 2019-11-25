package scw.orm.sql;

import scw.core.utils.StringUtils;
import scw.sql.orm.annotation.Table;

public class DefaultTableNameFactory implements TableNameFactory {

	public String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		return table == null ? StringUtils.humpNamingReplacement(clazz.getSimpleName(), "_") : table.name();
	}
}
