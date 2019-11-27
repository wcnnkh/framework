package scw.orm.sql.dialect.mysql;

import java.util.Iterator;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableFieldContext;

public final class InsertSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	protected static final String VALUES = ") values(";
	private String sql;
	private Object[] params;

	public InsertSQL(MappingOperations mappingOperations, Class<?> clazz, String tableName, Object obj)
			throws Exception {
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		this.params = new Object[tableFieldContext.size()];
		int index = 0;
		Iterator<MappingContext> iterator = tableFieldContext.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (SqlORMUtils.isAutoIncrement(context.getFieldDefinition())) {
				continue;
			}

			if (index++ > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, context.getFieldDefinition().getName());
			values.append("?");
			params[index++] = mappingOperations.getter(context, obj);
		}
		sql.append(INSERT_INTO_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append("(");
		sql.append(cols);
		sql.append(VALUES);
		sql.append(values);
		sql.append(")");
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return params;
	}
}
