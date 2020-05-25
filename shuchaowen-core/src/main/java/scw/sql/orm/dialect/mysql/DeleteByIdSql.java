package scw.sql.orm.dialect.mysql;

import java.util.Collection;
import java.util.Iterator;

import scw.lang.ParameterException;
import scw.sql.orm.Column;
import scw.sql.orm.ObjectRelationalMapping;

public class DeleteByIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public DeleteByIdSql(ObjectRelationalMapping objectRelationalMapping, Class<?> clazz, String tableName, Object[] parimayKeys) {
		Collection<Column> primaryKeys = objectRelationalMapping.getPrimaryKeys(clazz);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (primaryKeys.size() != parimayKeys.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);

		int i = 0;
		this.params = new Object[parimayKeys == null ? 0 : parimayKeys.length];
		Iterator<Column> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			keywordProcessing(sql, column.getName());
			sql.append("=?");
			params[i] = column.toDataBaseValue(parimayKeys[i]);
			i++;
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}
		this.sql = sql.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}