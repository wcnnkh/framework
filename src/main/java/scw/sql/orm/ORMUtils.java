package scw.sql.orm;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Year;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import scw.core.cglib.proxy.Factory;
import scw.core.instance.InstanceUtils;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.AnnotationUtils;
import scw.core.utils.ArrayUtils;
import scw.core.utils.ClassUtils;
import scw.core.utils.CompareUtils;
import scw.core.utils.FieldSetterListenUtils;
import scw.core.utils.IteratorCallback;
import scw.core.utils.ResourceUtils;
import scw.core.utils.StringUtils;
import scw.logger.LoggerUtils;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.NotColumn;
import scw.sql.orm.annotation.PrimaryKey;
import scw.sql.orm.annotation.Table;
import scw.sql.orm.annotation.Transient;
import scw.sql.orm.enums.CasType;

public final class ORMUtils {
	private ORMUtils() {
	};

	private volatile static IdentityHashMap<Class<?>, TableInfo> tableMap = new IdentityHashMap<Class<?>, TableInfo>();

	public static TableInfo getTableInfo(Class<?> clazz) {
		Class<?> clz = ClassUtils.getUserClass(clazz);
		TableInfo tableInfo = tableMap.get(clz);
		if (tableInfo == null) {
			synchronized (tableMap) {
				tableInfo = tableMap.get(clz);
				if (tableInfo == null) {
					tableInfo = new DefaultTableInfo(clz);
					tableMap.put(clz, tableInfo);
				}
			}
		}
		return tableInfo;
	}

	public static ColumnDefaultConfig getColumnDefaultConfig(Field field) {
		Class<?> type = field.getType();
		if (ClassUtils.isStringType(type)) {
			return new ColumnDefaultConfig("VARCHAR", 255);
		} else if (ClassUtils.isBooleanType(type)) {
			return new ColumnDefaultConfig("BIT", 1);
		} else if (ClassUtils.isByteType(type)) {
			return new ColumnDefaultConfig("TINYINT", 2);
		} else if (ClassUtils.isShortType(type)) {
			return new ColumnDefaultConfig("SMALLINT", 5);
		} else if (ClassUtils.isIntType(type)) {
			return new ColumnDefaultConfig("INTEGER", 10);
		} else if (ClassUtils.isLongType(type)) {
			return new ColumnDefaultConfig("BIGINT", 20);
		} else if (ClassUtils.isFloatType(type)) {
			return new ColumnDefaultConfig("FLOAT", 10);
		} else if (ClassUtils.isDoubleType(type)) {
			return new ColumnDefaultConfig("DOUBLE", 20);
		} else if (Date.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("DATE", 0);
		} else if (Timestamp.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("TIMESTAMP", 0);
		} else if (Time.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("TIME", 0);
		} else if (Year.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("YEAR", 0);
		} else if (Blob.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("BLOB", 0);
		} else if (Clob.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("CLOB", 0);
		} else if (BigDecimal.class.isAssignableFrom(type)) {
			return new ColumnDefaultConfig("NUMERIC", 0);
		} else {
			return new ColumnDefaultConfig("TEXT", 0);
		}
	}

	public static String getCharsetName(Field field) {
		Column column = field.getAnnotation(Column.class);
		return column == null ? null : column.charsetName().trim();
	}

	public static ColumnConvert getConvert(Field field) {
		Column column = field.getAnnotation(Column.class);
		Class<? extends ColumnConvert> convert = column == null ? DefaultColumnConvert.class : column.convert();
		if (column != null && !ArrayUtils.isEmpty(column.convertArgs())) {
			Object[] args = new Object[column.convertArgs().length];
			for (int i = 0; i < args.length; i++) {
				args[i] = column.convertArgs()[i];
			}
			return InstanceUtils.getInstance(convert, args);
		}
		return InstanceUtils.getSingleInstanceFactory().getInstance(convert);
	}

	public static boolean ignoreField(Field field) {
		if (AnnotationUtils.isDeprecated(field)) {
			return true;
		}

		NotColumn exclude = field.getAnnotation(NotColumn.class);
		if (exclude != null) {
			return true;
		}

		Transient tr = field.getAnnotation(Transient.class);
		if (tr != null) {
			return true;
		}

		if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())
				|| Modifier.isTransient(field.getModifiers())) {
			return true;
		}
		return false;
	}

	/**
	 * 将数据库值转化java类型
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static Object parse(Class<?> type, Object value) {
		if (value == null) {
			return value;
		}

		if (ClassUtils.isBooleanType(type)) {
			if (value != null) {
				if (value instanceof Number) {
					return ((Number) value).intValue() == 1;
				} else if (value instanceof String) {
					return StringUtils.parseBoolean((String) value);
				}
			}
		} else if (ClassUtils.isIntType(type)) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
		} else if (ClassUtils.isLongType(type)) {
			if (value instanceof Number) {
				return ((Number) value).longValue();
			}
		} else if (ClassUtils.isByteType(type)) {
			if (value instanceof Number) {
				return ((Number) value).byteValue();
			}
		} else if (ClassUtils.isFloatType(type)) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
			}
		} else if (ClassUtils.isDoubleType(type)) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			}
		} else if (ClassUtils.isShortType(type)) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			}
		}
		return value;
	}

	public static void registerCglibProxyTableBean(String pageName) {
		if (!StringUtils.isEmpty(pageName)) {
			LoggerUtils.info(ORMUtils.class, "register proxy package:{}", pageName);
		}

		for (Class<?> type : ResourceUtils.getClassList(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			FieldSetterListenUtils.createFieldSetterListenProxyClass(type);
		}
	}

	/**
	 * 获取主键数据
	 * 
	 * @param bean
	 * @param tableInfo
	 * @param parse
	 *            是否转化为数据库类型的值
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object[] getPrimaryKeys(Object bean, TableInfo tableInfo, boolean parse) throws Exception {
		ColumnInfo[] cs = tableInfo.getPrimaryKeyColumns();
		Object[] objs = new Object[cs.length];
		for (int i = 0; i < objs.length; i++) {
			if (parse) {
				objs[i] = cs[i].get(bean);
			} else {
				objs[i] = cs[i].getField().get(bean);
			}
		}
		return objs;
	}

	public static boolean isDataBaseField(Field field) {
		Column column = field.getAnnotation(Column.class);
		return column == null ? isDataBaseType(field.getType()) : true;
	}

	public static boolean isDataBaseType(Class<?> type) {
		return ClassUtils.isPrimitiveOrWrapper(type) || String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type) || Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type) || InputStream.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type) || Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type) || BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type) || NClob.class.isAssignableFrom(type)
				|| URL.class.isAssignableFrom(type) || byte[].class.isAssignableFrom(type);
	}

	public static String getDefaultTableName(Class<?> clazz) {
		char[] chars = clazz.getSimpleName().toCharArray();
		StringBuilder sb = new StringBuilder(chars.length);
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
		return sb.toString();
	}

	public static String getAnnotationTableName(Class<?> clazz) {
		String name = getDefaultTableName(clazz);
		Table table = clazz.getAnnotation(Table.class);
		if (table != null) {
			if (!"".equals(table.name())) {
				name = table.name();
			}
		}
		return name;
	}

	public static String getAnnotationColumnName(Field field) {
		String name = field.getName();
		Column column = field.getAnnotation(Column.class);
		if (column != null && column.name().length() != 0) {
			name = column.name();
		}
		return name;
	}

	public static CasType getCasType(Field field) {
		if (isAnnoataionPrimaryKey(field)) {
			return CasType.NOTHING;
		}

		Column column = field.getAnnotation(Column.class);
		if (column == null) {
			return CasType.NOTHING;
		}

		return column.casType();
	}

	public static String getAnnotationColumnTypeName(Field field) {
		Column column = field.getAnnotation(Column.class);
		return column == null ? null : column.type();
	}

	public static int getAnnotationColumnLength(Field field) {
		Column column = field.getAnnotation(Column.class);
		if (column == null) {
			return -1;
		}

		return column.length();
	}

	public static boolean isAnnoataionPrimaryKey(Field field) {
		return field.getAnnotation(PrimaryKey.class) != null;
	}

	public static boolean isIndexColumn(Field field) {
		return field.getAnnotation(Index.class) != null;
	}

	public static boolean isAnnoataionColumnNullAble(Field field) {
		if (field.getType().isPrimitive() || isAnnoataionPrimaryKey(field) || isIndexColumn(field)) {
			return false;
		}

		Column column = field.getAnnotation(Column.class);
		return column == null ? true : column.nullAble();
	}

	public static boolean isAnnoataionColumnUnique(Field field) {
		Column column = field.getAnnotation(Column.class);
		return column == null ? false : column.unique();
	}

	private static final class TableFieldMap extends LinkedHashMap<String, Field> {
		private static final long serialVersionUID = 1L;
		private final int order;

		public TableFieldMap(Class<?> table) {
			ReflectUtils.iteratorField(table, false, false, new IteratorCallback<Field>() {

				public boolean iteratorCallback(Field data) {
					if (ORMUtils.ignoreField(data)) {
						return true;
					}

					put(data.getName(), data);
					return true;
				}
			});
			Table t = table.getAnnotation(Table.class);
			this.order = t == null ? 0 : t.sort();
		}

		public int getOrder() {
			return order;
		}
	}

	public static LinkedList<Field> getFieldList(Class<?> table) {
		List<TableFieldMap> list = new LinkedList<ORMUtils.TableFieldMap>();
		Class<?> sup = table;
		while (sup != null && sup != Object.class) {
			if (sup == Factory.class) {
				sup = sup.getSuperclass();
				continue;
			}

			list.add(new TableFieldMap(sup));
			sup = sup.getSuperclass();
		}

		for (int i = list.size() - 1; i >= 0; i--) {
			TableFieldMap fieldMap = list.get(i);
			for (Entry<String, Field> entry : fieldMap.entrySet()) {
				for (int a = list.size() - 1; a > i; a--) {
					TableFieldMap tempFieldMap = list.get(a);
					tempFieldMap.remove(entry.getKey());
				}
			}
		}

		Collections.sort(list, new Comparator<TableFieldMap>() {

			public int compare(TableFieldMap table1, TableFieldMap table2) {
				if (table1.getOrder() == table2.getOrder()) {
					return CompareUtils.compare(getPrimaryKeySize(table1), getPrimaryKeySize(table2), false);
				}

				return CompareUtils.compare(table1.getOrder(), table2.getOrder(), false);
			}
		});
		
		LinkedList<Field> primaryKeyList = new LinkedList<Field>();
		LinkedList<Field> fieldList = new LinkedList<Field>();
		ListIterator<TableFieldMap> iterator = list.listIterator(list.size());
		while (iterator.hasPrevious()) {
			TableFieldMap tableFieldMap = iterator.previous();
			for(Entry<String, Field> entry : tableFieldMap.entrySet()){
				Field field = entry.getValue();
				if(isAnnoataionPrimaryKey(field)){
					primaryKeyList.add(field);
				}else{
					fieldList.add(field);
				}
			}
		}
		primaryKeyList.addAll(fieldList);
		return primaryKeyList;
	}

	private static int getPrimaryKeySize(TableFieldMap map) {
		int i = 0;
		for (Entry<String, Field> entry : map.entrySet()) {
			if (isAnnoataionPrimaryKey(entry.getValue())) {
				i++;
			}
		}
		return i;
	}
}
