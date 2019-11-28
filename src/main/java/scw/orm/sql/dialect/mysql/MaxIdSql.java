package scw.orm.sql.dialect.mysql;

import scw.orm.MappingContext;
import scw.orm.sql.SqlMapper;
import scw.orm.sql.TableMappingContext;

public class MaxIdSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public MaxIdSql(SqlMapper mappingOperations, Class<?> clazz, String tableName, String idField)
			throws Exception {
		TableMappingContext tableFieldContext = mappingOperations.getTableMappingContext(clazz);
		String columnName;
		MappingContext context = tableFieldContext.getMappingContext(idField);
		columnName = context == null ? idField : context.getColumn().getName();
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
