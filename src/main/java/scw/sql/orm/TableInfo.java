package scw.sql.orm;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.core.ClassInfo;
import scw.core.FieldInfo;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.sql.orm.annotation.NotColumn;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.annotation.Transient;

public final class TableInfo {
	private String name;
	private final Table table;
	private String engine = "InnoDB";
	private String charset = "utf8";
	private String row_format = "COMPACT";

	private final ClassInfo classInfo;

	private final Map<String, ColumnInfo> columnMap;// 所有的
	// 数据库字段名到字段的映射
	private final Map<String, String> fieldToColumn;// 所有的

	private final ColumnInfo[] columns;
	private final ColumnInfo[] primaryKeyColumns;
	private final ColumnInfo[] notPrimaryKeyColumns;
	private final ColumnInfo[] tableColumns;
	private boolean parent = false;
	private ColumnInfo autoIncrement;
	private final ColumnInfo[] autoCreateColumns;

	TableInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
		StringBuilder sb = new StringBuilder();
		char[] chars;
		try {
			chars = Class.forName(ClassUtils.getProxyRealClassName(classInfo.getSource())).getSimpleName()
					.toCharArray();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(classInfo.getSource().getName());
		}

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isUpperCase(c)) {// 如果是大写的
				if (i != 0) {
					sb.append("_");
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}

		this.name = sb.toString();

		this.table = classInfo.getSource().getAnnotation(Table.class);
		if (table != null) {
			if (!"".equals(table.name())) {
				this.name = table.name();
			}

			if (!StringUtils.isNull(table.engine())) {
				this.engine = table.engine();
			}

			if (!StringUtils.isNull(table.charset())) {
				this.charset = table.charset();
			}

			if (!StringUtils.isNull(table.row_format())) {
				this.row_format = table.row_format();
			}

			this.parent = table.parent();
		}

		List<ColumnInfo> allColumnList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> idNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> notIdNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> tableColumnList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> autoCreateColumnList = new ArrayList<ColumnInfo>();

		Map<String, ColumnInfo> columnMap = new HashMap<String, ColumnInfo>();
		Map<String, String> fieldToColumn = new HashMap<String, String>();

		ClassInfo tempClassInfo = classInfo;
		while (tempClassInfo != null) {
			for (FieldInfo fieldInfo : tempClassInfo.getFieldInfos()) {
				NotColumn exclude = fieldInfo.getField().getAnnotation(NotColumn.class);
				if (exclude != null) {
					continue;
				}

				Transient tr = fieldInfo.getField().getAnnotation(Transient.class);
				if (tr != null) {
					continue;
				}

				if (Modifier.isStatic(fieldInfo.getField().getModifiers())
						|| Modifier.isFinal(fieldInfo.getField().getModifiers())
						|| Modifier.isTransient(fieldInfo.getField().getModifiers())) {
					continue;
				}

				ColumnInfo columnInfo = new ColumnInfo(name, fieldInfo);
				if (columnMap.containsKey(columnInfo.getName()) || fieldToColumn.containsKey(fieldInfo.getName())) {
					throw new AlreadyExistsException("[" + columnInfo.getName() + "]字段已存在");
				}

				columnMap.put(columnInfo.getName(), columnInfo);
				fieldToColumn.put(fieldInfo.getName(), columnInfo.getName());

				if (columnInfo.isDataBaseType()) {
					allColumnList.add(columnInfo);
					if (columnInfo.getPrimaryKey() != null) {
						idNameList.add(columnInfo);
					} else {
						notIdNameList.add(columnInfo);
					}

					if (columnInfo.getAutoIncrement() != null) {
						if (autoIncrement != null) {
							throw new RuntimeException(classInfo.getSource().getName() + "存在多个@AutoIncrement字段");
						}

						autoIncrement = columnInfo;
					}

					if (columnInfo.getAutoCreate() != null) {
						autoCreateColumnList.add(columnInfo);
					}
				} else {
					boolean javaType = fieldInfo.getField().getType().getName().startsWith("java.")
							|| fieldInfo.getField().getType().getName().startsWith("javax.");
					if (!javaType) {
						tableColumnList.add(columnInfo);
					}
				}
			}

			boolean parent = true;
			Table table = tempClassInfo.getSource().getAnnotation(Table.class);
			if (table != null) {
				parent = table.parent();
			}

			if (!parent) {
				break;
			}
			tempClassInfo = tempClassInfo.getSuperInfo();
		}

		this.columns = allColumnList.toArray(new ColumnInfo[allColumnList.size()]);
		this.primaryKeyColumns = idNameList.toArray(new ColumnInfo[0]);
		this.notPrimaryKeyColumns = notIdNameList.toArray(new ColumnInfo[0]);
		this.tableColumns = tableColumnList.toArray(new ColumnInfo[tableColumnList.size()]);
		this.autoCreateColumns = autoCreateColumnList.toArray(new ColumnInfo[autoCreateColumnList.size()]);
		this.columnMap = new HashMap<String, ColumnInfo>(columnMap.size(), 1);
		this.columnMap.putAll(columnMap);
		this.fieldToColumn = new HashMap<String, String>(fieldToColumn.size(), 1);
		this.fieldToColumn.putAll(fieldToColumn);
	}

	public String getDefaultName() {
		return name;
	}

	public String getEngine() {
		return engine;
	}

	public String getCharset() {
		return charset;
	}

	public String getRow_format() {
		return row_format;
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

	public Table getTable() {
		return table;
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
		return table != null;
	}

	/*
	 * 这些字段都是实体类，并且对应着表
	 * 
	 * @return
	 */
	public ColumnInfo[] getTableColumns() {
		return tableColumns;
	}

	public ClassInfo getClassInfo() {
		return classInfo;
	}

	public boolean isParent() {
		return parent;
	}

	public Object[] getPrimaryKeyParameter(Object data) throws IllegalArgumentException, IllegalAccessException {
		Object[] params = new Object[getPrimaryKeyColumns().length];
		for (int i = 0; i < params.length; i++) {
			params[i] = getPrimaryKeyColumns()[i].getFieldInfo().forceGet(data);
		}
		return params;
	}

	@SuppressWarnings("unchecked")
	public <T> T newInstance() {
		if (table == null) {
			return (T) ReflectUtils.newInstance(classInfo.getSource());
		} else {
			return (T) classInfo.newFieldListenInstance();
		}
	}

	public ColumnInfo getAutoIncrement() {
		return autoIncrement;
	}

	public ColumnInfo[] getAutoCreateColumns() {
		return autoCreateColumns;
	}

	public <T extends Annotation> T getAnnotation(Class<T> type) {
		return classInfo.getSource().getAnnotation(type);
	}

	public String getName(Object bean) {
		if (bean instanceof TableName) {
			return ((TableName) bean).tableName();
		}

		return name;
	}
}
