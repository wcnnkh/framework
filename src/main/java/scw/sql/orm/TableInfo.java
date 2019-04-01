package scw.sql.orm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import scw.beans.BeanFieldListen;
import scw.beans.BeanUtils;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.exception.AlreadyExistsException;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.sql.orm.annoation.NotColumn;
import scw.sql.orm.annoation.Table;
import scw.sql.orm.annoation.Transient;

public final class TableInfo {
	private String name;
	private Table table;
	private String engine = "InnoDB";
	private String charset = "utf8";
	private String row_format = "COMPACT";

	private ClassInfo classInfo;

	private Map<String, ColumnInfo> columnMap = new HashMap<String, ColumnInfo>();// 所有的
																					// 数据库字段名到字段的映射
	private Map<String, String> fieldToColumn = new HashMap<String, String>();// 所有的
																				// 字段名数据库名的映射
	// 字段的set方法，如果没有set方法就不到这个集合里面
	// 用来做监听非主键字段的更新
	private Map<String, ColumnInfo> notPrimaryKeySetterNameMap = new HashMap<String, ColumnInfo>();

	private ColumnInfo[] columns;
	private ColumnInfo[] primaryKeyColumns;
	private ColumnInfo[] notPrimaryKeyColumns;
	private ColumnInfo[] tableColumns;
	private Class<?>[] proxyInterface;
	private boolean parent = false;
	private ColumnInfo autoIncrement;

	TableInfo(ClassInfo classInfo) {
		this.classInfo = classInfo;
		StringBuilder sb = new StringBuilder();
		char[] chars;
		try {
			chars = Class.forName(ClassUtils.getProxyRealClassName(classInfo.getClz())).getSimpleName().toCharArray();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(classInfo.getClz().getName());
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

		this.table = classInfo.getClz().getAnnotation(Table.class);
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

		ClassInfo tempClassInfo = classInfo;
		while (tempClassInfo != null) {
			for (String fieldName : tempClassInfo.getFieldNames()) {
				FieldInfo fieldInfo = tempClassInfo.getFieldMap().get(fieldName);
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

				this.columnMap.put(columnInfo.getName(), columnInfo);
				this.fieldToColumn.put(fieldInfo.getName(), columnInfo.getName());

				if (columnInfo.isDataBaseType()) {
					allColumnList.add(columnInfo);
					if (columnInfo.getPrimaryKey() != null) {
						idNameList.add(columnInfo);
					} else {
						notIdNameList.add(columnInfo);
						if (fieldInfo.getSetter() != null) {
							this.notPrimaryKeySetterNameMap.put(fieldInfo.getSetter().getName(), columnInfo);
						}
					}

					if (columnInfo.getAutoIncrement() != null) {
						if (autoIncrement != null) {
							throw new RuntimeException(classInfo.getName() + "存在多个@AutoIncrement字段");
						}

						autoIncrement = columnInfo;
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
			Table table = tempClassInfo.getClz().getAnnotation(Table.class);
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
	}

	public String getName() {
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

	public ColumnInfo getColumnByNotPrimaryKeySetterNameMap(String setterMethodName) {
		return notPrimaryKeySetterNameMap.get(setterMethodName);
	}

	public Class<?>[] getProxyInterface() {
		return proxyInterface;
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
			try {
				return (T) classInfo.getClz().newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return null;
		} else {
			BeanFieldListen beanFieldListen = (BeanFieldListen) BeanUtils.newFieldListenInstance(classInfo.getClz());
			beanFieldListen.start_field_listen();
			return (T) beanFieldListen;
		}
	}

	public ColumnInfo getAutoIncrement() {
		return autoIncrement;
	}
}
