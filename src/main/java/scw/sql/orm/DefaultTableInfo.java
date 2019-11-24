package scw.sql.orm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class DefaultTableInfo extends AbstractTableInfo {
	private final Map<String, ColumnInfo> columnMap;// 所有的
	// 数据库字段名到字段的映射
	private final Map<String, String> fieldToColumn;// 所有的

	private final ColumnInfo[] columns;
	private final ColumnInfo[] primaryKeyColumns;
	private final ColumnInfo[] notPrimaryKeyColumns;
	private final ColumnInfo[] tableColumns;
	private ColumnInfo autoIncrement;

	public DefaultTableInfo(Class<?> clz) {
		super(clz);
		final Map<String, ColumnInfo> tempColumnMap = new LinkedHashMap<String, ColumnInfo>();
		final Map<String, String> tempFieldToColumn = new LinkedHashMap<String, String>();
		for (Field field : ORMUtils.getFieldList(getSource())) {
			ColumnInfo columnInfo = new DefaultColumnInfo(field);
			tempColumnMap.remove(columnInfo.getName());
			tempColumnMap.put(columnInfo.getName(), columnInfo);
			tempFieldToColumn.put(field.getName(), columnInfo.getName());
		}

		this.columnMap = new HashMap<String, ColumnInfo>(tempColumnMap.size(), 1);
		this.columnMap.putAll(tempColumnMap);
		this.fieldToColumn = new HashMap<String, String>(tempFieldToColumn.size(), 1);
		this.fieldToColumn.putAll(tempFieldToColumn);

		final List<ColumnInfo> allColumnList = new ArrayList<ColumnInfo>();
		final List<ColumnInfo> idNameList = new ArrayList<ColumnInfo>();
		final List<ColumnInfo> notIdNameList = new ArrayList<ColumnInfo>();
		final List<ColumnInfo> tableColumnList = new ArrayList<ColumnInfo>();
		for (Entry<String, ColumnInfo> entry : tempColumnMap.entrySet()) {
			ColumnInfo columnInfo = entry.getValue();
			if (columnInfo.isDataBaseType()) {
				allColumnList.add(columnInfo);
				if (columnInfo.isPrimaryKey()) {
					idNameList.add(columnInfo);
				} else {
					notIdNameList.add(columnInfo);
				}

				if (columnInfo.isAutoIncrement()) {
					if (autoIncrement != null) {
						throw new RuntimeException(getSource().getName() + "存在多个@AutoIncrement字段");
					}

					autoIncrement = columnInfo;
				}
			} else {
				boolean javaType = columnInfo.getField().getType().getName().startsWith("java.")
						|| columnInfo.getField().getType().getName().startsWith("javax.");
				if (!javaType) {
					tableColumnList.add(columnInfo);
				}
			}
		}

		this.columns = allColumnList.toArray(new ColumnInfo[allColumnList.size()]);
		this.primaryKeyColumns = idNameList.toArray(new ColumnInfo[idNameList.size()]);
		this.notPrimaryKeyColumns = notIdNameList.toArray(new ColumnInfo[notIdNameList.size()]);
		this.tableColumns = tableColumnList.toArray(new ColumnInfo[tableColumnList.size()]);
	}

	public ColumnInfo getColumnInfo(String fieldName) {
		ColumnInfo columnInfo = columnMap.get(fieldName);
		if (columnInfo == null) {
			String v = fieldToColumn.get(fieldName);
			if (v == null) {
				throw new NullPointerException(
						"not found table[" + getDefaultName() + "] fieldName[" + fieldName + "]");
			}

			columnInfo = columnMap.get(v);
		}
		return columnInfo;
	}

	public ColumnInfo[] getColumns() {
		return columns;
	}

	public ColumnInfo[] getPrimaryKeyColumns() {
		return primaryKeyColumns;
	}

	public ColumnInfo[] getNotPrimaryKeyColumns() {
		return notPrimaryKeyColumns;
	}

	/*
	 * 这些字段都是实体类，并且对应着表
	 * 
	 * @return
	 */
	public ColumnInfo[] getTableColumns() {
		return tableColumns;
	}

	public ColumnInfo getAutoIncrement() {
		return autoIncrement;
	}
}
