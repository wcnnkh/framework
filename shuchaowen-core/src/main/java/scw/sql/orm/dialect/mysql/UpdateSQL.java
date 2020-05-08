package scw.sql.orm.dialect.mysql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.sql.orm.Column;
import scw.sql.orm.ObjectRelationalMapping;
import scw.sql.orm.enums.CasType;

public class UpdateSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public UpdateSQL(ObjectRelationalMapping objectRelationalMapping, Class<?> clazz, Object obj, String tableName) {
		Collection<Column> primaryKeys = objectRelationalMapping.getPrimaryKeys(clazz);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException(tableName + " not found primary key");
		}
		
		Collection<Column> notPrimaryKeys = objectRelationalMapping.getNotPrimaryKeys(clazz);
		StringBuilder sb = new StringBuilder(512);
		sb.append(UPDATE_PREFIX);
		keywordProcessing(sb, tableName);
		sb.append(SET);
		List<Object> params = new ArrayList<Object>(notPrimaryKeys.size());
		Iterator<Column> iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sb, column.getName());
			if (column.getCasType() == CasType.AUTO_INCREMENT) {
				sb.append("=");
				keywordProcessing(sb, column.getName());
				sb.append("+1");
			} else {
				sb.append("=?");
				params.add(column.get(obj));
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		sb.append(WHERE);
		iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(column.get(obj));
			if (iterator.hasNext()) {
				sb.append(AND);
			}
		}

		iterator = notPrimaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.getCasType() == CasType.NOTHING) {
				continue;
			}

			sb.append(AND);
			keywordProcessing(sb, column.getName());
			sb.append("=?");
			params.add(column.get(obj));
		}
		this.sql = sb.toString();
		this.params = params.toArray(new Object[params.size()]);
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
