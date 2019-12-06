package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import scw.core.utils.MultiIterator;
import scw.orm.MappingContext;
import scw.orm.ObjectRelationalMapping;
import scw.orm.sql.SqlMapper;

public final class InsertSQL extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	protected static final String VALUES = ") values(";
	private String sql;
	private Object[] params;

	public InsertSQL(SqlMapper mappingOperations, Class<?> clazz, String tableName, Object obj) throws Exception {
		ObjectRelationalMapping tableFieldContext = mappingOperations.getObjectRelationalMapping(clazz);
		StringBuilder cols = new StringBuilder();
		StringBuilder values = new StringBuilder();
		StringBuilder sql = new StringBuilder();
		List<Object> params = new ArrayList<Object>();
		@SuppressWarnings("unchecked")
		Iterator<MappingContext> iterator = new MultiIterator<MappingContext>(
				tableFieldContext.getPrimaryKeys().iterator(), tableFieldContext.getNotPrimaryKeys().iterator());
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (mappingOperations.isAutoIncrement(context)) {
				continue;
			}

			if (cols.length() > 0) {
				cols.append(",");
				values.append(",");
			}

			keywordProcessing(cols, context.getColumn().getName());
			values.append("?");
			params.add(mappingOperations.getter(context, obj));
		}
		sql.append(INSERT_INTO_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append("(");
		sql.append(cols);
		sql.append(VALUES);
		sql.append(values);
		sql.append(")");
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
