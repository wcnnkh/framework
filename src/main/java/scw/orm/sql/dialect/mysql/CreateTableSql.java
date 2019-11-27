package scw.orm.sql.dialect.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.reflect.FieldDefinition;
import scw.core.utils.StringUtils;
import scw.orm.MappingContext;
import scw.orm.sql.SqlMappingOperations;
import scw.orm.sql.SqlORMUtils;
import scw.orm.sql.TableMappingContext;
import scw.orm.sql.annotation.Column;
import scw.orm.sql.annotation.Index;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.dialect.DefaultSqlTypeFactory;
import scw.orm.sql.dialect.SqlType;
import scw.orm.sql.dialect.SqlTypeFactory;
import scw.orm.sql.enums.IndexMethod;
import scw.orm.sql.enums.IndexOrder;

public class CreateTableSql extends MysqlDialectSql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public CreateTableSql(SqlMappingOperations mappingOperations, Class<?> clazz, String tableName) {
		this(mappingOperations, clazz, tableName, new DefaultSqlTypeFactory());
	}

	public CreateTableSql(SqlMappingOperations mappingOperations, Class<?> clazz, String tableName,
			final SqlTypeFactory sqlTypeFactory) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("`");
		sb.append(" (");

		TableMappingContext tableFieldContext = mappingOperations.getTableMappingContext(clazz);
		Iterator<MappingContext> iterator = tableFieldContext.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			FieldDefinition fieldDefinition = context.getFieldDefinition();
			SqlType sqlType = SqlORMUtils.getSqlType(fieldDefinition, sqlTypeFactory);
			sb.append("`").append(fieldDefinition.getName()).append("`");
			sb.append(" ");
			sb.append(sqlType.getName());
			if (sqlType.getLength() > 0) {
				sb.append("(").append(sqlType.getLength()).append(")");
			}
			sb.append(" ");

			if (!StringUtils.isEmpty(SqlORMUtils.getCharsetName(fieldDefinition))) {
				sb.append("character set ").append(SqlORMUtils.getCharsetName(fieldDefinition)).append(" ");
			}

			if (!SqlORMUtils.isNullAble(fieldDefinition)) {
				sb.append("not null ");
			}

			Column column = fieldDefinition.getAnnotation(Column.class);
			if (column != null && !StringUtils.isEmpty(column.comment())) {
				sb.append(" comment \'").append(column.comment()).append("\'");
			}

			if (SqlORMUtils.isAutoIncrement(fieldDefinition)) {
				sb.append(" AUTO_INCREMENT");
			}

			if (iterator.hasNext()) {
				sb.append(",");
			}
		}

		iterator = tableFieldContext.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (!SqlORMUtils.isUnique(context.getFieldDefinition())) {
				continue;
			}

			sb.append(",");
			sb.append("UNIQUE (");
			sb.append("`").append(context.getFieldDefinition().getName()).append("`");
			sb.append(")");
		}

		final Map<String, List<IndexInfo>> indexMap = new LinkedHashMap<String, List<IndexInfo>>();
		final Map<String, Index> indexConfigMap = new HashMap<String, Index>();
		iterator = tableFieldContext.iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			Index index = context.getFieldDefinition().getAnnotation(Index.class);
			if (index == null) {
				continue;
			}

			if (!indexConfigMap.containsKey(index.name())) {
				indexConfigMap.put(index.name(), index);
			}

			List<IndexInfo> indexList = indexMap.get(index.name());
			if (indexList == null) {
				indexList = new ArrayList<IndexInfo>();
				indexMap.put(index.name(), indexList);
			}
			indexList.add(new IndexInfo(context.getFieldDefinition().getName(), index));
		}

		for (Entry<String, List<IndexInfo>> entry : indexMap.entrySet()) {
			sb.append(",");

			Index index = indexConfigMap.get(entry.getKey());
			if (index.method() != IndexMethod.DEFAULT) {
				sb.append(" ");
				sb.append(index.method().name());
			}

			sb.append(" INDEX");

			if (!StringUtils.isEmpty(index.name())) {
				sb.append(" ");
				sb.append(index.name());
			}

			sb.append(" (");
			Iterator<IndexInfo> indexIterator = entry.getValue().iterator();
			while (indexIterator.hasNext()) {
				IndexInfo indexInfo = indexIterator.next();
				sb.append(indexInfo.getColumn());
				if (indexInfo.getIndex().length() != -1) {
					sb.append("(");
					sb.append(index.length());
					sb.append(")");
				}

				if (indexInfo.getIndex().order() != IndexOrder.DEFAULT) {
					sb.append(" ").append(indexInfo.getIndex().order().name());
				}

				if (indexIterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");
		}

		StringBuilder primaryKeySql = new StringBuilder();
		iterator = tableFieldContext.getPrimaryKeys().iterator();
		while (iterator.hasNext()) {
			MappingContext context = iterator.next();
			if (primaryKeySql.length() > 0) {
				primaryKeySql.append(",");
			}
			primaryKeySql.append("`");
			primaryKeySql.append(context.getFieldDefinition().getName());
			primaryKeySql.append("`");
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

class IndexInfo {
	private String column;
	private Index index;

	public IndexInfo(String column, Index index) {
		this.column = column;
		this.index = index;
	}

	public String getColumn() {
		return column;
	}

	public Index getIndex() {
		return index;
	}
}
