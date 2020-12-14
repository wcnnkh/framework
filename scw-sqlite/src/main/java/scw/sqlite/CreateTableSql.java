package scw.sqlite;

import java.util.Iterator;
import java.util.Set;

import scw.core.utils.StringUtils;
import scw.sql.orm.Column;
import scw.sql.orm.Columns;
import scw.sql.orm.OrmUtils;
import scw.sql.orm.dialect.DialectHelper;
import scw.sql.orm.dialect.DialectSql;
import scw.sql.orm.dialect.SqlType;
import scw.sql.orm.dialect.SqlTypeFactory;

public class CreateTableSql extends DialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	private SqlType getSqlType(Column column, SqlTypeFactory sqlTypeFactory) {
		SqlType sqlType = column.getSqlType(sqlTypeFactory);
		if (sqlType.getName() == SqlTypeFactory.BIGINT.getName()) {
			sqlType = SqlTypeFactory.INTEGER;
		}
		return sqlType;
	}

	public CreateTableSql(Class<?> clazz, String tableName,
			DialectHelper dialectHelper) {
		StringBuilder sb = new StringBuilder();
		sb.append(dialectHelper.getCreateTablePrefix());
		sb.append(" ");
		dialectHelper.keywordProcessing(sb, tableName);
		sb.append(" (");

		Columns columns = OrmUtils.getObjectRelationalMapping().getColumns(
				clazz);
		Iterator<scw.sql.orm.Column> iterator = columns.iterator();
		Set<Column> primaryKeys = columns.getPrimaryKeys();
		while (iterator.hasNext()) {
			scw.sql.orm.Column col = iterator.next();
			dialectHelper.keywordProcessing(sb, col.getName());

			sb.append(" ");

			SqlType sqlType = getSqlType(col, dialectHelper.getSqlTypeFactory());
			sb.append(sqlType.getName());

			if (primaryKeys.size() == 1) {
				if (col.isPrimaryKey()) {
					sb.append(" PRIMARY KEY");
				}

				if (col.isAutoIncrement()) {
					sb.append(" AUTOINCREMENT");
				}
			}

			if (col.isUnique()) {
				sb.append(" UNIQUE");
			}

			if (!col.isNullable()) {
				sb.append(" not null");
			}

			if (!StringUtils.isEmpty(col.getCharsetName())) {
				sb.append(" character set ").append(col.getCharsetName());
			}

			if (StringUtils.isNotEmpty(col.getDescription())) {
				sb.append(" comment \'").append(col.getDescription())
						.append("\'");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		// primary keys
		if (primaryKeys.size() > 1) {
			// 多主键
			sb.append(",primary key(");
			iterator = primaryKeys.iterator();
			while (iterator.hasNext()) {
				Column column = iterator.next();
				dialectHelper.keywordProcessing(sb, column.getName());
				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		sb.append(")");
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
}