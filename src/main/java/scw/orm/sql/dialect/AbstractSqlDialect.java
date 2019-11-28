package scw.orm.sql.dialect;

import scw.core.Pagination;
import scw.core.utils.StringUtils;
import scw.sql.SimpleSql;
import scw.sql.Sql;

public abstract class AbstractSqlDialect implements SqlDialect {

	public PaginationSql toPaginationSql(Sql sql, long page, int limit) throws SqlDialectException {
		String str = sql.getSql();
		int fromIndex = str.indexOf(" from ");// ignore select
		if (fromIndex == -1) {
			fromIndex = str.indexOf(" FROM ");
		}

		if (fromIndex == -1) {
			throw new IndexOutOfBoundsException(str);
		}

		String whereSql;
		int orderIndex = str.lastIndexOf(" order by ");
		if (orderIndex == -1) {
			orderIndex = str.lastIndexOf(" ORDER BY ");
		}

		if (orderIndex == -1) {// 不存在 order by 子语句
			whereSql = str.substring(fromIndex);
		} else {
			whereSql = str.substring(fromIndex, orderIndex);
		}

		Sql countSql = new SimpleSql("select count(*)" + whereSql, sql.getParams());
		StringBuilder sb = new StringBuilder(str);
		sb.append(" limit ").append(Pagination.getBegin(page, limit)).append(",").append(limit);
		return new PaginationSql(countSql, new SimpleSql(sb.toString(), sql.getParams()));
	}

	public Sql toCopyTableStructureSql(String newTableName, String oldTableName) throws SqlDialectException {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(newTableName).append("`");
		sb.append(" like `").append(oldTableName).append("`");
		return new SimpleSql(sb.toString());
	}

	public String getTableName(Class<?> clazz, Object obj, String tableName) {
		String tName = tableName;
		if (StringUtils.isEmpty(tName)) {
			if (obj instanceof TableName) {
				tName = ((TableName) obj).getTableName();
			}
		}
		return StringUtils.isEmpty(tName) ? getSqlMapper().getTableName(clazz) : tName;
	}

	public String getTableName(Class<?> clazz, String tableName) {
		return (tableName == null || tableName.length() == 0) ? getSqlMapper().getTableName(clazz)
				: tableName;
	}
}
