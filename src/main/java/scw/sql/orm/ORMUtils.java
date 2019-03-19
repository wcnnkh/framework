package scw.sql.orm;

import java.util.HashMap;
import java.util.Map;

import scw.beans.BeanFieldListen;
import scw.beans.BeanUtils;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.orm.annoation.Table;

public abstract class ORMUtils {
	private static Logger logger = LoggerFactory.getLogger(ORMUtils.class);
	
	private volatile static Map<String, TableInfo> tableMap = new HashMap<String, TableInfo>();

	public static TableInfo getTableInfo(Class<?> clz) {
		return getTableInfo(clz.getName());
	}

	private static TableInfo getTableInfo(String className) {
		String name = ClassUtils.getProxyRealClassName(className);
		TableInfo tableInfo = tableMap.get(name);
		if (tableInfo == null) {
			synchronized (tableMap) {
				tableInfo = tableMap.get(name);
				if (tableInfo == null) {
					tableInfo = new TableInfo(ClassUtils.getClassInfo(name));
					tableMap.put(name, tableInfo);
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
					return ((Number) value).doubleValue() == 1;
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
		logger.debug("register proxy package:{}", pageName);
		for (Class<?> type : ClassUtils.getClasses(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (BeanFieldListen.class.isAssignableFrom(type)) {
				continue;
			}

			BeanUtils.getFieldListenProxyClass(type);
		}
	}

	public static String getTableName(String tableName, TableInfo tableInfo, Object obj) {
		if (StringUtils.isEmpty(tableName)) {
			if (obj instanceof TableName) {
				return ((TableName) obj).tableName();
			} else {
				return tableInfo.getName();
			}
		} else {
			return tableName;
		}
	}
	
	
}
