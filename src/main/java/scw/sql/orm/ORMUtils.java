package scw.sql.orm;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.sql.orm.annotation.Table;

public final class ORMUtils {
	private ORMUtils() {
	};

	private static Logger logger = LoggerFactory.getLogger(ORMUtils.class);

	private volatile static Map<Class<?>, TableInfo> tableMap = new HashMap<Class<?>, TableInfo>();

	public static TableInfo getTableInfo(Class<?> clazz) {
		Class<?> clz = ClassUtils.getUserClass(clazz);
		TableInfo tableInfo = tableMap.get(clz);
		if (tableInfo == null) {
			synchronized (tableMap) {
				tableInfo = tableMap.get(clz);
				if (tableInfo == null) {
					tableInfo = new TableInfo(clz);
					tableMap.put(clz, tableInfo);
				}
			}
		}
		return tableInfo;
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
		logger.info("register proxy package:{}", pageName);
		for (Class<?> type : ClassUtils.getClasses(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}
			getTableInfo(type);
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
	public static Object[] getPrimaryKeys(Object bean, TableInfo tableInfo, boolean parse)
			throws IllegalArgumentException, IllegalAccessException {
		ColumnInfo[] cs = tableInfo.getPrimaryKeyColumns();
		Object[] objs = new Object[cs.length];
		for (int i = 0; i < objs.length; i++) {
			if (parse) {
				objs[i] = cs[i].getValueToDB(bean);
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

	public static Class<?>[] getTableFieldListenProxyInterfaces(Class<?> clazz) {
		Class<?>[] interfaces;
		if (TableFieldListen.class.isAssignableFrom(clazz)) {
			interfaces = clazz.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = clazz.getInterfaces();
			if (arr.length == 0) {
				interfaces = new Class[] { TableFieldListen.class };
			} else {
				interfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, interfaces, 0, arr.length);
				interfaces[arr.length] = TableFieldListen.class;
			}
		}
		return interfaces;
	}
}
