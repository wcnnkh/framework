package scw.orm.sql.dialect.mysql;

import scw.orm.MappingContext;
import scw.orm.MappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableFieldContext;

public class MaxIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public MaxIdSql(MappingOperations mappingOperations, Class<?> clazz, String tableName, String idField)
			throws Exception {
		TableFieldContext tableFieldContext = SqlORMUtils.getTableFieldContext(mappingOperations, clazz);
		String columnName;
		MappingContext context = tableFieldContext.getMappingContext(idField);
		columnName = context == null ? idField : context.getFieldDefinition().getName();
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		keywordProcessing(sb, columnName);
		sb.append(" from ");
		keywordProcessing(sb, tableName);
		sb.append(" order by ");
		keywordProcessing(sb, columnName);
		sb.append(" desc");
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
}
