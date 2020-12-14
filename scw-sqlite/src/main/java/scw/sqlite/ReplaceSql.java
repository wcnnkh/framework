package scw.sqlite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import scw.sql.orm.Column;
import scw.sql.orm.OrmUtils;
import scw.sql.orm.dialect.DialectHelper;
import scw.sql.orm.dialect.DialectSql;
import scw.value.AnyValue;

public class ReplaceSql extends DialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public ReplaceSql(Class<?> clazz, Object obj, String tableName, DialectHelper dialectHelper) {
		Collection<Column> primaryKeys = OrmUtils.getObjectRelationalMapping().getColumns(clazz).getColumns();
		if (primaryKeys.size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		StringBuilder sb = new StringBuilder(512);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		Iterator<Column> iterator = OrmUtils.getObjectRelationalMapping().getColumns(clazz).iterator();
		while (iterator.hasNext()) {
			Column column = iterator.next();
			Object value = column.get(obj);
			if(column.isAutoIncrement()){
				AnyValue anyValue = new AnyValue(value);
				if(value == null || anyValue.isEmpty() || (anyValue.isNumber() && anyValue.getAsInteger() == 0)){
					continue;
				}
			}
			
			dialectHelper.keywordProcessing(cols, column.getName());
			values.append("?");
			params.add(value);

			if (iterator.hasNext()) {
				cols.append(",");
				values.append(",");
			}
		}

		sb.append("replace into ");
		dialectHelper.keywordProcessing(sb, tableName);
		sb.append("(");
		sb.append(cols);
		sb.append(VALUES);
		sb.append(values);
		sb.append(")");
		this.sql = sb.toString();
		this.params = params.toArray();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
