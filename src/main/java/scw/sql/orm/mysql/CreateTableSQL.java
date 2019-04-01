package scw.sql.orm.mysql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.utils.StringUtils;
import scw.sql.Sql;
import scw.sql.orm.ColumnInfo;
import scw.sql.orm.TableInfo;
import scw.sql.orm.annoation.Index;
import scw.sql.orm.enums.IndexMethod;
import scw.sql.orm.enums.IndexOrder;

public final class CreateTableSQL implements Sql {
	private static final long serialVersionUID = 1L;
	private String sql;

	public CreateTableSQL(TableInfo tableInfo, String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("`");
		sb.append(" (");

		for (int i = 0; i < tableInfo.getColumns().length; i++) {
			if (i > 0) {
				sb.append(",");
			}

			ColumnInfo columnInfo = tableInfo.getColumns()[i];
			int len = columnInfo.getLength();
			sb.append(columnInfo.getSqlColumnName());
			sb.append(" ");
			if ("java.lang.String".equals(columnInfo.getTypeName())) {
				sb.append("varchar");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(255)");
				}
			} else if ("float".equals(columnInfo.getTypeName()) || "java.lang.Float".equals(columnInfo.getTypeName())) {
				sb.append("float");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(10)");
				}
			} else if ("double".equals(columnInfo.getTypeName())
					|| "java.lang.Double".equals(columnInfo.getTypeName())) {
				sb.append("double");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(20)");
				}
			} else if ("long".equals(columnInfo.getTypeName()) || "java.lang.Long".equals(columnInfo.getTypeName())) {
				sb.append("bigint");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(20)");
				}
			} else if ("int".equals(columnInfo.getTypeName()) || "java.lang.Integer".equals(columnInfo.getTypeName())) {
				sb.append("int");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(10)");
				}
			} else if ("short".equals(columnInfo.getTypeName()) || "java.lang.Short".equals(columnInfo.getTypeName())) {
				sb.append("SMALLINT");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(5)");
				}
			} else if ("byte".equals(columnInfo.getTypeName()) || "java.lang.Byte".equals(columnInfo.getTypeName())) {
				sb.append("bit");
				if (len > 0) {
					sb.append("(").append(len).append(")");
				} else {
					sb.append("(1)");
				}
			} else if ("boolean".equals(columnInfo.getTypeName())
					|| "java.lang.Boolean".equals(columnInfo.getTypeName())) {
				sb.append("bit(1)");
			} else {
				sb.append(columnInfo.getTypeName());
				if (len > 0) {
					sb.append("(").append(len).append(")");
				}
			}
			sb.append(" ");

			if (!columnInfo.isNullAble()) {
				sb.append("not null ");
			}

			if (columnInfo.getColumn() != null && !StringUtils.isEmpty(columnInfo.getColumn().comment())) {
				sb.append(" comment \'").append(columnInfo.getColumn().comment()).append("\'");
			}

			if (columnInfo.getAutoIncrement() != null) {
				sb.append(" AUTO_INCREMENT");
			}
		}

		for (int i = 0; i < tableInfo.getColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getColumns()[i];
			if (!columnInfo.isUnique()) {
				continue;
			}

			sb.append(",");
			sb.append("UNIQUE (");
			sb.append(columnInfo.getSqlColumnName());
			sb.append(")");
		}

		Map<String, List<IndexInfo>> indexMap = new HashMap<String, List<IndexInfo>>();
		Map<String, Index> indexConfigMap = new HashMap<String, Index>();
		for (int i = 0; i < tableInfo.getColumns().length; i++) {
			ColumnInfo columnInfo = tableInfo.getColumns()[i];
			Index index = columnInfo.getFieldInfo().getField().getAnnotation(Index.class);
			if (index == null) {
				continue;
			}

			if (!indexConfigMap.containsKey(columnInfo.getSqlColumnName())) {
				indexConfigMap.put(columnInfo.getSqlColumnName(), index);
			}

			List<IndexInfo> indexList = indexMap.get(index.name());
			if (indexList == null) {
				indexList = new ArrayList<IndexInfo>();
				indexMap.put(index.name(), indexList);
			}
			indexList.add(new IndexInfo(columnInfo.getSqlColumnName(), index));
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
			Iterator<IndexInfo> iterator = entry.getValue().iterator();
			while (iterator.hasNext()) {
				IndexInfo indexInfo = iterator.next();
				sb.append(indexInfo.getColumn());
				if (indexInfo.getIndex().length() != -1) {
					sb.append("(");
					sb.append(index.length());
					sb.append(")");
				}

				if (indexInfo.getIndex().order() != IndexOrder.DEFAULT) {
					sb.append(" ").append(indexInfo.getIndex().order().name());
				}

				if (iterator.hasNext()) {
					sb.append(",");
				}
			}
			sb.append(")");

		}

		if (tableInfo.getPrimaryKeyColumns().length > 0) {
			sb.append(",");
			sb.append("primary key (");
			for (int i = 0; i < tableInfo.getPrimaryKeyColumns().length; i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(tableInfo.getPrimaryKeyColumns()[i].getSqlColumnName());
			}
			sb.append(")");
		}

		sb.append(")");
		sb.append(" ENGINE=").append(tableInfo.getEngine());
		sb.append(" DEFAULT");
		sb.append(" CHARSET=").append(tableInfo.getCharset());
		sb.append(" ROW_FORMAT=").append(tableInfo.getRow_format());
		if (tableInfo.getTable() != null && !StringUtils.isEmpty(tableInfo.getTable().comment())) {
			sb.append(" comment=\'").append(tableInfo.getTable().comment()).append("\'");

		}
		this.sql = sb.toString();
	}

	public String getSql() {
		return sql;
	}

	public Object[] getParams() {
		return null;
	}

	public boolean isStoredProcedure() {
		return false;
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
