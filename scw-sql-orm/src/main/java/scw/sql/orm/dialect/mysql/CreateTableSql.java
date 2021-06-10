package scw.sql.orm.dialect.mysql;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import scw.core.utils.StringUtils;
import scw.orm.sql.annotation.IndexMethod;
import scw.orm.sql.annotation.IndexOrder;
import scw.orm.sql.annotation.Table;
import scw.sql.orm.Column;
import scw.sql.orm.Columns;
import scw.sql.orm.IndexInfo;
import scw.sql.orm.OrmUtils;
import scw.sql.orm.dialect.DialectHelper;
import scw.sql.orm.dialect.DialectSql;
import scw.sql.orm.dialect.SqlType;

public class CreateTableSql extends DialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

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
			SqlType sqlType = col.getSqlType(dialectHelper.getSqlTypeFactory());
			dialectHelper.keywordProcessing(sb, col.getName());

			sb.append(" ");
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(").append(sqlType.getLength()).append(")");
			}

			if (!StringUtils.isEmpty(col.getCharsetName())) {
				sb.append(" character set ").append(col.getCharsetName());
			}

			if(primaryKeys.size() == 1){
				if (col.isPrimaryKey()) {
					sb.append(" PRIMARY KEY");
				}

				if (col.isAutoIncrement()) {
					sb.append(" AUTO_INCREMENT");
				}
			}

			if (col.isUnique()) {
				sb.append(" UNIQUE");
			}

			if (!col.isNullable()) {
				sb.append(" not null");
			}

			if (StringUtils.isNotEmpty(col.getDescription())) {
				sb.append(" comment \'").append(col.getDescription())
						.append("\'");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		for (Entry<IndexInfo, List<IndexInfo>> entry : OrmUtils
				.getObjectRelationalMapping().getIndexInfoMap(clazz).entrySet()) {
			sb.append(",");
			if (entry.getKey().getMethod() != IndexMethod.DEFAULT) {
				sb.append(" ");
				sb.append(entry.getKey().getMethod().name());
			}

			sb.append(" INDEX");

			if (!StringUtils.isEmpty(entry.getKey().getName())) {
				sb.append(" ");
				dialectHelper.keywordProcessing(sb, entry.getKey().getName());
			}

			sb.append(" (");
			Iterator<IndexInfo> indexIterator = entry.getValue().iterator();
			while (indexIterator.hasNext()) {
				IndexInfo indexInfo = indexIterator.next();
				dialectHelper.keywordProcessing(sb, indexInfo.getColumn()
						.getName());
				if (indexInfo.getLength() != -1) {
					sb.append("(");
					sb.append(indexInfo.getLength());
					sb.append(")");
				}

				if (indexInfo.getOrder() != IndexOrder.DEFAULT) {
					sb.append(" ");
					dialectHelper.keywordProcessing(sb, indexInfo.getOrder()
							.name());
				}

				if (indexIterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}
		
		//primary keys
		if(primaryKeys.size() > 1){
			//多主键
			sb.append(",primary key(");
			iterator = primaryKeys.iterator();
			while(iterator.hasNext()){
				Column column = iterator.next();
				dialectHelper.keywordProcessing(sb, column.getName());
				if(iterator.hasNext()){
					sb.append(",");
				}
			}
			sb.append(")");
		}
		sb.append(")");

		Table table = clazz.getAnnotation(Table.class);
		if (table != null) {
			if (StringUtils.isNotEmpty(table.engine())) {
				sb.append(" ENGINE=").append(table.engine());
			}

			if (StringUtils.isNotEmpty(table.charset())) {
				sb.append(" CHARSET=").append(table.charset());
			}

			if (StringUtils.isNotEmpty(table.row_format())) {
				sb.append(" ROW_FORMAT=").append(table.row_format());
			}
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