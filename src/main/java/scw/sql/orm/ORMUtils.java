package scw.sql.orm;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.IdentityHashMap;

import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.StringUtils;
import scw.sql.orm.annotation.Column;
import scw.sql.orm.annotation.Index;
import scw.sql.orm.annotation.PrimaryKey;
import scw.sql.orm.annotation.Table;

public final class ORMUtils {
	private ORMUtils() {
	};

	private static Logger logger = LoggerFactory.getLogger(ORMUtils.class);

	private volatile static IdentityHashMap<Class<?>, TableInfo> tableMap = new IdentityHashMap<Class<?>, TableInfo>();

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
	public static Object[] getPrimaryKeys(Object bean, TableInfo tableInfo,
			boolean parse) throws Exception{
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
		return ClassUtils.isPrimitiveOrWrapper(type)
				|| String.class.isAssignableFrom(type)
				|| Date.class.isAssignableFrom(type)
				|| Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type)
				|| InputStream.class.isAssignableFrom(type)
				|| Array.class.isAssignableFrom(type)
				|| Blob.class.isAssignableFrom(type)
				|| Clob.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type)
				|| Reader.class.isAssignableFrom(type)
				|| NClob.class.isAssignableFrom(type)
				|| URL.class.isAssignableFrom(type)
				|| byte[].class.isAssignableFrom(type);
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

	public static boolean isAnnoataionColumnNullAble(Field field) {
		boolean nullAble;
		Column column = field.getAnnotation(Column.class);
		if (column != null) {
			if (column.unique() || isAnnoataionPrimaryKey(field)
					|| field.getAnnotation(Index.class) != null) {
				nullAble = false;
				if (column.nullAble()) {
					logger.warn("字段{}不能或不推荐设置为允许为空，因为他可能是主键或索引",
							field.getName());
				}
			} else {
				nullAble = column.nullAble();
			}
		} else {
			if (isAnnoataionPrimaryKey(field)
					|| field.getAnnotation(Index.class) != null) {
				nullAble = false;
			} else {
				nullAble = !field.getType().isPrimitive();
			}
		}
		return nullAble;
	}

	public static boolean isAnnoataionColumnUnique(Field field) {
		Column column = field.getAnnotation(Column.class);
		return column == null ? false : column.unique();
	}
}
