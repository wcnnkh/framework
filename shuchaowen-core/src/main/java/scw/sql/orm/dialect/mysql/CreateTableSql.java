package scw.sql.orm.dialect.mysql;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import scw.core.utils.StringUtils;
import scw.sql.SqlUtils;
import scw.sql.orm.IndexInfo;
import scw.sql.orm.ObjectRelationalMapping;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.dialect.SqlType;
import scw.sql.orm.dialect.SqlTypeFactory;
import scw.sql.orm.enums.IndexMethod;
import scw.sql.orm.enums.IndexOrder;

public class CreateTableSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public CreateTableSql(ObjectRelationalMapping objectRelationalMapping,
			Class<?> clazz, String tableName,
			final SqlTypeFactory sqlTypeFactory) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("`");
		sb.append(" (");

		Iterator<scw.sql.orm.Column> iterator = objectRelationalMapping
				.getColumns(clazz).iterator();
		while (iterator.hasNext()) {
			scw.sql.orm.Column col = iterator.next();
			SqlType sqlType = col.getSqlType(sqlTypeFactory);
			sb.append("`").append(col.getName()).append("`");
			sb.append(" ");
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(").append(sqlType.getLength()).append(")");
			}
			sb.append(" ");

			if (!StringUtils.isEmpty(col.getCharsetName())) {
				sb.append("character set ").append(col.getCharsetName())
						.append(" ");
			}

			if (!col.isNullable()) {
				sb.append("not null ");
			}

			if (StringUtils.isNotEmpty(col.getDescription())) {
				sb.append(" comment \'").append(col.getDescription())
						.append("\'");
			}

			if (col.isAutoIncrement()) {
				sb.append(" AUTO_INCREMENT");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		iterator = objectRelationalMapping.getColumns(clazz).iterator();
		while (iterator.hasNext()) {
			scw.sql.orm.Column column = iterator.next();
			if (!column.isUnique()) {
				continue;
			}

			sb.append(",");
			sb.append("UNIQUE (");
			sb.append("`").append(column.getName()).append("`");
			sb.append(")");
		}

		for (Entry<IndexInfo, List<IndexInfo>> entry : objectRelationalMapping
				.getIndexInfoMap(clazz).entrySet()) {
			sb.append(",");
			if (entry.getKey().getMethod() != IndexMethod.DEFAULT) {
				sb.append(" ");
				sb.append(entry.getKey().getMethod().name());
			}

			sb.append(" INDEX");

			if (!StringUtils.isEmpty(entry.getKey().getName())) {
				sb.append(" ");
				sb.append(entry.getKey().getName());
			}

			sb.append(" (");
			Iterator<IndexInfo> indexIterator = entry.getValue().iterator();
			while (indexIterator.hasNext()) {
				IndexInfo indexInfo = indexIterator.next();
				sb.append(indexInfo.getColumn().getName());
				if (indexInfo.getLength() != -1) {
					sb.append("(");
					sb.append(indexInfo.getLength());
					sb.append(")");
				}

				if (indexInfo.getOrder() != IndexOrder.DEFAULT) {
					sb.append(" ").append(indexInfo.getOrder().name());
				}

				if (indexIterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		StringBuilder primaryKeySql = new StringBuilder();
		iterator = SqlUtils.getObjectRelationalMapping().getColumns(clazz)
				.iterator();
		while (iterator.hasNext()) {
			scw.sql.orm.Column column = iterator.next();
			if (!column.isPrimaryKey()) {
				continue;
			}
			if (primaryKeySql.length() > 0) {
				primaryKeySql.append(",");
			}

			keywordProcessing(primaryKeySql, column.getName());
		}

		if (primaryKeySql.length() > 0) {
			sb.append(",");
			sb.append("primary key (");
			sb.append(primaryKeySql);
			sb.append(")");
		}

		sb.append(")");
		Table table = clazz.getAnnotation(Table.class);
		if (table != null) {
			sb.append(" ENGINE=").append(table.engine());
			sb.append(" DEFAULT");
			sb.append(" CHARSET=").append(table.charset());
			sb.append(" ROW_FORMAT=").append(table.row_format());
		}

		if (table != null && !StringUtils.isEmpty(table.comment())) {
			sb.append(" comment=\'").append(table.comment()).append("\'");
		}
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}
}