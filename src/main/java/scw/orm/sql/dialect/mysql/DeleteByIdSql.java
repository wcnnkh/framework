package scw.orm.sql.dialect.mysql;

import java.util.Iterator;

import scw.core.exception.ParameterException;
import scw.orm.MappingContext;
import scw.orm.SimpleGetter;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableMappingContext;

public class DeleteByIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public DeleteByIdSql(SqlMapper mappingOperations, Class<?> clazz, String tableName, Object[] parimayKeys)
			throws Exception {
		TableMappingContext tableFieldContext = mappingOperations.getTableMappingContext(clazz);
		if (tableFieldContext.getPrimaryKeys().size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableFieldContext.getPrimaryKeys().size() != parimayKeys.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);

		int i = 0;
		this.params = new Object[parimayKeys == null ? 0 : parimayKeys.length];
		Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sql, context.getColumn().getName());
			sql.append("=?");
			params[i] = mappingOperations.getter(context, new SimpleGetter(parimayKeys[i]));
			i++;
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
