package scw.orm.sql.dialect.mysql;

import java.util.Iterator;

import scw.core.exception.ParameterException;
import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableFieldContext;

public class DeleteByIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;
	private Object[] params;

	public DeleteByIdSql(MappingOperations mappingOperations, Class<?> clazz, String tableName, Object[] parimayKeys)
			throws Exception {
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		if (tableFieldContext.getPrimaryKeys().size() == 0) {
			throw new NullPointerException("not found primary key");
		}

		if (tableFieldContext.getPrimaryKeys().size() != parimayKeys.length) {
			throw new ParameterException("主键数量不一致:" + tableName);
		}

		this.params = parimayKeys;

		StringBuilder sql = new StringBuilder();
		sql.append(DELETE_PREFIX);
		keywordProcessing(sql, tableName);
		sql.append(WHERE);

		Iterator<MappingContext> iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			keywordProcessing(sql, context.getFieldDefinition().getName());
			sql.append("=?");
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
