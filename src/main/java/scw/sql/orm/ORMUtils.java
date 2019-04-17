package scw.sql.orm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import scw.common.BeanFieldListen;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringParseUtils;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.orm.annotation.Table;

public abstract class ORMUtils {
	private static Logger logger = LoggerFactory.getLogger(ORMUtils.class);

	private volatile static Map<Class<?>, TableInfo> tableMap = new HashMap<Class<?>, TableInfo>();

	public static TableInfo getTableInfo(Class<?> clz) {
		TableInfo tableInfo = tableMap.get(clz);
		if(tableInfo == null){
			synchronized (tableMap) {
				tableInfo = tableMap.get(clz);
				if(tableInfo == null){
					tableInfo = new TableInfo(ClassUtils.getClassInfo(clz));
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
					return ((Number) value).doubleValue() == 1;
				} else if (value instanceof String) {
					return StringParseUtils.parseBoolean((String) value);
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

			if (BeanFieldListen.class.isAssignableFrom(type)) {
				continue;
			}

			ClassInfo classInfo = ClassUtils.getClassInfo(type);
			classInfo.createFieldListenProxyClass();
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

	/**
	 * 重新监听
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T restartFieldLinsten(T bean) throws Exception {
		if (bean == null) {
			return bean;
		}

		if (bean instanceof BeanFieldListen) {
			((BeanFieldListen) bean).start_field_listen();
			return bean;
		} else {
			ClassInfo classInfo = ClassUtils.getClassInfo(bean.getClass());
			BeanFieldListen proxy = (BeanFieldListen) classInfo.newFieldListenInstance();
			for (Entry<String, FieldInfo> entry : classInfo.getFieldMap().entrySet()) {
				FieldInfo fieldInfo = entry.getValue();
				if (fieldInfo.isStatic()) {
					continue;
				}

				Object v = fieldInfo.get(bean);
				if (v != null) {
					fieldInfo.set(proxy, v);
				}
			}
			proxy.start_field_listen();
			return (T) proxy;
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
				objs[i] = cs[i].getFieldInfo().forceGet(bean);
			}
		}
		return objs;
	}
}
