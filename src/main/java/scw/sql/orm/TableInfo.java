package scw.sql.orm;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;
import scw.core.exception.AlreadyExistsException;
import scw.core.reflect.ReflectUtils;
import scw.sql.orm.annotation.NotColumn;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.annotation.Transient;

public final class TableInfo {
	private String name;
	private final Class<?> source;
	private final Map<String, ColumnInfo> columnMap;// 所有的
	// 数据库字段名到字段的映射
	private final Map<String, String> fieldToColumn;// 所有的

	private final ColumnInfo[] columns;
	private final ColumnInfo[] primaryKeyColumns;
	private final ColumnInfo[] notPrimaryKeyColumns;
	private final ColumnInfo[] tableColumns;
	private ColumnInfo autoIncrement;
	private final ColumnInfo[] autoCreateColumns;
	private final Map<String, Field> fieldSetterMethodMap;
	private final Class<?>[] beanListenInterfaces;
	private final boolean serializer;
	private final boolean table;

	TableInfo(Class<?> clz) {
		this.source = clz;
		this.serializer = Serializable.class.isAssignableFrom(source);
		if (BeanFieldListen.class.isAssignableFrom(source)) {
			beanListenInterfaces = source.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = source.getInterfaces();
			if (arr.length == 0) {
				beanListenInterfaces = new Class[] { BeanFieldListen.class };
			} else {
				beanListenInterfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, beanListenInterfaces, 0, arr.length);
				beanListenInterfaces[arr.length] = BeanFieldListen.class;
			}
		}

		StringBuilder sb = new StringBuilder();
		char[] chars = source.getSimpleName().toCharArray();
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

		Table table = source.getAnnotation(Table.class);
		this.table = table != null;
		if (table != null) {
			if (!"".equals(table.name())) {
				this.name = table.name();
			}
		}

		List<ColumnInfo> allColumnList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> idNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> notIdNameList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> tableColumnList = new ArrayList<ColumnInfo>();
		List<ColumnInfo> autoCreateColumnList = new ArrayList<ColumnInfo>();

		Map<String, ColumnInfo> columnMap = new HashMap<String, ColumnInfo>();
		Map<String, String> fieldToColumn = new HashMap<String, String>();

		Map<String, Field> fieldSetterMethodMap = new HashMap<String, Field>();
		Class<?> tempClassInfo = clz;
		while (tempClassInfo != null) {
			for (Field field : source.getDeclaredFields()) {
				Deprecated deprecated = field.getAnnotation(Deprecated.class);
				if (deprecated != null) {
					continue;
				}

				NotColumn exclude = field.getAnnotation(NotColumn.class);
				if (exclude != null) {
					continue;
				}

				Transient tr = field.getAnnotation(Transient.class);
				if (tr != null) {
					continue;
				}

				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())
						|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}

				ColumnInfo columnInfo = new ColumnInfo(name, field);
				if (columnMap.containsKey(columnInfo.getName()) || fieldToColumn.containsKey(field.getName())) {
					throw new AlreadyExistsException("[" + columnInfo.getName() + "]字段已存在");
				}

				field.setAccessible(true);
				Method method = ReflectUtils.getSetterMethod(tempClassInfo, field, false);
				if (method != null) {
					fieldSetterMethodMap.put(method.getName(), field);
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

					if (columnInfo.getAutoIncrement() != null) {
						if (autoIncrement != null) {
							throw new RuntimeException(source.getName() + "存在多个@AutoIncrement字段");
						}

						autoIncrement = columnInfo;
					}

					if (columnInfo.getAutoCreate() != null) {
						autoCreateColumnList.add(columnInfo);
					}
				} else {
					boolean javaType = field.getType().getName().startsWith("java.")
							|| field.getType().getName().startsWith("javax.");
					if (!javaType) {
						tableColumnList.add(columnInfo);
					}
				}
			}

			boolean parent = true;
			Table myTable = tempClassInfo.getAnnotation(Table.class);
			if (myTable != null) {
				parent = myTable.parent();
			}

			if (!parent) {
				break;
			}
			tempClassInfo = tempClassInfo.getSuperclass();
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
		this.fieldSetterMethodMap = new HashMap<String, Field>(fieldSetterMethodMap.size(), 1);
		this.fieldSetterMethodMap.putAll(fieldSetterMethodMap);

		getProxyClass();
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
			Enhancer enhancer = new Enhancer();
			enhancer.setInterfaces(beanListenInterfaces);
			enhancer.setCallback(new FieldListenMethodInterceptor());
			enhancer.setSuperclass(source);
			if (serializer) {
				enhancer.setSerialVersionUID(1L);
			}

			BeanFieldListen beanFieldListen = (BeanFieldListen) enhancer.create();
			beanFieldListen.start_field_listen();
			return (T) beanFieldListen;
		} else {
			return ReflectUtils.newInstance(source);
		}
	}

	private volatile Class<? extends BeanFieldListen> proxyClass;

	@SuppressWarnings("unchecked")
	public Class<? extends BeanFieldListen> getProxyClass() {
		if (proxyClass == null) {
			synchronized (this) {
				if (proxyClass == null) {
					Enhancer enhancer = new Enhancer();
					enhancer.setInterfaces(beanListenInterfaces);
					enhancer.setCallbackType(FieldListenMethodInterceptor.class);
					enhancer.setSuperclass(source);
					if (serializer) {
						enhancer.setSerialVersionUID(1L);
					}
					this.proxyClass = enhancer.createClass();
				}
			}
		}
		return proxyClass;
	}

	public ColumnInfo getAutoIncrement() {
		return autoIncrement;
	}

	public ColumnInfo[] getAutoCreateColumns() {
		return autoCreateColumns;
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

	public Field getFieldInfoBySetterName(String setterName) {
		return fieldSetterMethodMap.get(setterName);
	}
}
