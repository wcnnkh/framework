package scw.sql.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.instance.InstanceUtils;
import scw.core.utils.FieldSetterListenUtils;
import scw.core.utils.IteratorCallback;
import scw.sql.orm.annotation.Table;

final class DefaultTableInfo implements TableInfo {
	private final String name;
	private final Class<?> source;
	private final Map<String, ColumnInfo> columnMap;// 所有的
	// 数据库字段名到字段的映射
	private final Map<String, String> fieldToColumn;// 所有的

	private final ColumnInfo[] columns;
	private final ColumnInfo[] primaryKeyColumns;
	private final ColumnInfo[] notPrimaryKeyColumns;
	private final ColumnInfo[] tableColumns;
	private ColumnInfo autoIncrement;
	private final boolean table;

	public DefaultTableInfo(Class<?> clz) {
		this.source = clz;
		this.name = ORMUtils.getAnnotationTableName(source);
		Table table = source.getAnnotation(Table.class);
		this.table = table != null;

		final Map<String, ColumnInfo> tempColumnMap = new LinkedHashMap<String, ColumnInfo>();
		final Map<String, String> tempFieldToColumn = new LinkedHashMap<String, String>();
		ORMUtils.iterator(source, new IteratorCallback<Field>() {

			public boolean iteratorCallback(Field field) {
				ColumnInfo columnInfo = new DefaultColumnInfo(field);
				tempColumnMap.remove(columnInfo.getName());
				tempColumnMap.put(columnInfo.getName(), columnInfo);
				tempFieldToColumn.put(field.getName(), columnInfo.getName());
				return true;
			}
		});
		this.columnMap = new HashMap<String, ColumnInfo>(tempColumnMap.size(),
				1);
		this.columnMap.putAll(tempColumnMap);
		this.fieldToColumn = new HashMap<String, String>(
				tempFieldToColumn.size(), 1);
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
						throw new RuntimeException(source.getName()
								+ "存在多个@AutoIncrement字段");
					}

					autoIncrement = columnInfo;
				}
			} else {
				boolean javaType = columnInfo.getField().getType().getName()
						.startsWith("java.")
						|| columnInfo.getField().getType().getName()
								.startsWith("javax.");
				if (!javaType) {
					tableColumnList.add(columnInfo);
				}
			}
		}

		this.columns = allColumnList.toArray(new ColumnInfo[allColumnList
				.size()]);
		this.primaryKeyColumns = idNameList.toArray(new ColumnInfo[idNameList
				.size()]);
		this.notPrimaryKeyColumns = notIdNameList
				.toArray(new ColumnInfo[notIdNameList.size()]);
		this.tableColumns = tableColumnList
				.toArray(new ColumnInfo[tableColumnList.size()]);
	}

	public String getDefaultName() {
		return name;
	}

	public ColumnInfo getColumnInfo(String fieldName) {
		ColumnInfo columnInfo = columnMap.get(fieldName);
		if (columnInfo == null) {
			String v = fieldToColumn.get(fieldName);
			if (v == null) {
				throw new NullPointerException("not found table[" + this.name
						+ "] fieldName[" + fieldName + "]");
			}

			columnInfo = columnMap.get(v);
		}
		return columnInfo;
	}

	public Map<String, String> getFieldToColumn() {
		return fieldToColumn;
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

	public boolean isTable() {
		return table;
	}

	/*
	 * 这些字段都是实体类，并且对应着表
	 * 
	 * @return
	 */
	public ColumnInfo[] getTableColumns() {
		return tableColumns;
	}

	public Class<?> getSource() {
		return source;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if (table) {
			try {
				return (T) FieldSetterListenUtils
						.newFieldSetterListenInstance(source);
			} catch (Throwable e) {
				return InstanceUtils.newInstance(source);
			}
		} else {
			return InstanceUtils.newInstance(source);
		}
	}

	public ColumnInfo getAutoIncrement() {
		return autoIncrement;
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return source.getAnnotation(type);
	}

	public String getName(Object bean) {
		if (bean instanceof TableName) {
			return ((TableName) bean).tableName();
		}

		return name;
	}
}
