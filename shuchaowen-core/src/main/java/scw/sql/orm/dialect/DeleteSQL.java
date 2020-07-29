package scw.sql.orm.dialect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.sql.SqlUtils;
import scw.sql.orm.Column;
import scw.sql.orm.enums.CasType;

public class DeleteSQL extends DialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public <T> DeleteSQL(Class<? extends T> clazz, T obj, String tableName, DialectHelper dialectHelper) {
		Collection<Column> primaryKeys = SqlUtils.getObjectRelationalMapping().getPrimaryKeys(clazz);
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		List<Object> params = new ArrayList<Object>(primaryKeys.size());
		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		dialectHelper.keywordProcessing(sql, tableName);
		sql.append(WHERE);
		Iterator<Column> iterator = primaryKeys.iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			dialectHelper.keywordProcessing(sql, column.getName());
			sql.append("=?");
			params.add(column.get(obj));
			if (iterator.hasNext()) {
				sql.append(AND);
			}
		}

		iterator = SqlUtils.getObjectRelationalMapping().getNotPrimaryKeys(clazz).iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			if (column.getCasType() == CasType.NOTHING) {
				continue;
			}

			sql.append(AND);
			dialectHelper.keywordProcessing(sql, column.getName());
			sql.append("=?");
			params.add(column.get(obj));
		}
		this.sql = sql.toString();
		this.params = params.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
