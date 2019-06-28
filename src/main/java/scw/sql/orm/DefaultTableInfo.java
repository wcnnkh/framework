package scw.sql.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.FieldSetterListen;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.FieldSetterListenUtils;
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

		List<ColumnInfo> allColumnList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> idNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> notIdNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> tableColumnList = new ArrayList<ColumnInfo>();

		Map<String, ColumnInfo> columnMap = new HashMap<String, ColumnInfo>();
		Map<String, String> fieldToColumn = new HashMap<String, String>();

		Class<?> tempClassInfo = clz;
		while (tempClassInfo != null && tempClassInfo != Object.class) {
			for (Field field : ReflectUtils.getFieldMapUseCache(tempClassInfo).values()) {
				if (ORMUtils.ignoreField(field)) {
					continue;
				}

				ColumnInfo columnInfo = new DefaultColumnInfo(name, field);
				if (columnMap.containsKey(columnInfo.getName()) || fieldToColumn.containsKey(field.getName())) {
					throw new AlreadyExistsException(source.getName() + "中[" + columnInfo.getName() + "]字段已存在");
				}

				columnMap.put(columnInfo.getName(), columnInfo);
				fieldToColumn.put(field.getName(), columnInfo.getName());

				if (columnInfo.isDataBaseType()) {
					allColumnList.add(columnInfo);
					if (columnInfo.isPrimaryKey()) {
						idNameList.add(columnInfo);
					} else {
						notIdNameList.add(columnInfo);
					}

					if (columnInfo.isAutoIncrement()) {
						if (autoIncrement != null) {
							throw new RuntimeException(source.getName() + "存在多个@AutoIncrement字段");
						}

						autoIncrement = columnInfo;
					}
				} else {
					boolean javaType = field.getType().getName().startsWith("java.")
							|| field.getType().getName().startsWith("javax.");
					if (!javaType) {
						tableColumnList.add(columnInfo);
					}
				}
			}
			tempClassInfo = tempClassInfo.getSuperclass();
		}

		this.columns = allColumnList.toArray(new ColumnInfo[allColumnList.size()]);
		this.primaryKeyColumns = idNameList.toArray(new ColumnInfo[0]);
		this.notPrimaryKeyColumns = notIdNameList.toArray(new ColumnInfo[0]);
		this.tableColumns = tableColumnList.toArray(new ColumnInfo[tableColumnList.size()]);
		this.columnMap = new HashMap<String, ColumnInfo>(columnMap.size(), 1);
		this.columnMap.putAll(columnMap);
		this.fieldToColumn = new HashMap<String, String>(fieldToColumn.size(), 1);
		this.fieldToColumn.putAll(fieldToColumn);

		if (this.table) {
			getProxyClass();
		}
	}

	public String getDefaultName() {
		return name;
	}

	public ColumnInfo getColumnInfo(String fieldName) {
		ColumnInfo columnInfo = columnMap.get(fieldName);
		if (columnInfo == null) {
			String v = fieldToColumn.get(fieldName);
			if (v == null) {
				throw new NullPointerException("not found table[" + this.name + "] fieldName[" + fieldName + "]");
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

	public Object[] getPrimaryKeyParameter(Object data) throws IllegalArgumentException, IllegalAccessException {
		Object[] params = new Object[getPrimaryKeyColumns().length];
		for (int i = 0; i < params.length; i++) {
			params[i] = getPrimaryKeyColumns()[i].getField().get(data);
		}
		return params;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if (table) {
			return (T) FieldSetterListenUtils.newFieldSetterListenInstance(source);
		} else {
			return ReflectUtils.newInstance(source);
		}
	}

	public Class<? extends FieldSetterListen> getProxyClass() {
		return FieldSetterListenUtils.createFieldSetterListenProxyClass(source);
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
