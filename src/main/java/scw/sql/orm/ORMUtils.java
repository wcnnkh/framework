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
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import scw.core.cglib.proxy.Factory;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.AnnotationUtils;
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

	public static Object get(Field field, Object bean) throws IllegalArgumentException, IllegalAccessException {
		Object value = field.get(bean);
		if (boolean.class == field.getType()) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == field.getType()) {
			if (value == null) {
				return null;
			}
			return (Boolean) value ? 1 : 0;
		}
		return value;
	}

	public static void set(Field field, Object bean, Object value)
			throws IllegalArgumentException, IllegalAccessException {
		field.set(bean, parse(field.getType(), value));
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
		if(!StringUtils.isEmpty(pageName)){
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
				objs[i] = get(cs[i].getField(), bean);
			} else {
				objs[i] = cs[i].getField().get(bean);
			}
		}
		return objs;
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
		String typeName = field.getType().getName();
		Column column = field.getAnnotation(Column.class);
		if (column != null && column.type().length() != 0) {
			typeName = column.type();
		}
		return typeName;
	}

	public static int getAnnotationColumnLength(Field field) {
		Column column = field.getAnnotation(Column.class);
		return column == null ? -1 : column.length();
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

	private static void iteratorTable(Class<?> table, final IteratorCallback<Field> iteratorCallback) {
		ReflectUtils.iteratorField(table, false, false, new IteratorCallback<Field>() {

			public boolean iteratorCallback(Field data) {
				if (ORMUtils.ignoreField(data)) {
					return true;
				}

				return iteratorCallback.iteratorCallback(data);
			}
		});
	}

	public static void iterator(Class<?> table, IteratorCallback<Field> iteratorCallback) {
		Class<?> sup = table;
		LinkedList<Class<?>> list = new LinkedList<Class<?>>();
		while (sup != null && sup != Object.class) {
			if (sup == Factory.class) {
				sup = sup.getSuperclass();
				continue;
			}

			list.add(sup);
			sup = sup.getSuperclass();
		}

		Collections.sort(list, new Comparator<Class<?>>() {

			public int compare(Class<?> table1, Class<?> table2) {
				Table t1 = table1.getAnnotation(Table.class);
				Table t2 = table2.getAnnotation(Table.class);
				return CompareUtils.compare(t1 == null ? 0 : t1.sort(), t2 == null ? 0 : t2.sort(), false);
			}
		});

		ListIterator<Class<?>> iterator = list.listIterator(list.size());
		while (iterator.hasPrevious()) {
			iteratorTable(iterator.previous(), iteratorCallback);
		}
	}
}
