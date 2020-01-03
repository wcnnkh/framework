package scw.orm.sql.support;

import scw.core.utils.StringUtils;
import scw.orm.sql.TableNameMapping;
import scw.orm.sql.annotation.Table;

public class DefaultTableNameMapping implements TableNameMapping {

	public String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			return StringUtils.humpNamingReplacement(clazz.getSimpleName(), "_");
		}

		if (StringUtils.isEmpty(table.name())) {
			return StringUtils.humpNamingReplacement(clazz.getSimpleName(), "_");
		}

		return table.name();
	}

}
