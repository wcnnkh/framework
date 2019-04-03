package scw.sql.orm;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.cglib.proxy.Enhancer;
import scw.common.ClassInfo;
import scw.common.FieldInfo;
import scw.common.utils.ClassUtils;
import scw.common.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.sql.orm.annotation.Table;

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
		logger.info("register proxy package:{}", pageName);
		for (Class<?> type : ClassUtils.getClasses(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (BeanFieldListen.class.isAssignableFrom(type)) {
				continue;
			}

			getFieldListenProxyClass(type);
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

	public static Enhancer createFieldListenEnhancer(Class<?> clz) {
		ClassInfo classInfo = ClassUtils.getClassInfo(clz);
		Class<?>[] beanListenInterfaces;
		if (BeanFieldListen.class.isAssignableFrom(clz)) {
			beanListenInterfaces = clz.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = clz.getInterfaces();
			if (arr.length == 0) {
				beanListenInterfaces = new Class[] { BeanFieldListen.class };
			} else {
				beanListenInterfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, beanListenInterfaces, 0, arr.length);
				beanListenInterfaces[arr.length] = BeanFieldListen.class;
			}
		}

		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(beanListenInterfaces);
		if (classInfo.getSerialVersionUID() != null) {
			enhancer.setSerialVersionUID(classInfo.getSerialVersionUID());
		}

		enhancer.setCallback(new FieldListenMethodInterceptor());
		enhancer.setSuperclass(clz);
		return enhancer;
	}

	public static Class<?> getFieldListenProxyClass(Class<?> clz) {
		ClassInfo classInfo = ClassUtils.getClassInfo(clz);
		Class<?>[] beanListenInterfaces;
		if (BeanFieldListen.class.isAssignableFrom(clz)) {
			beanListenInterfaces = clz.getInterfaces();
		} else {// 没有自己实现此接口，增加此接口
			Class<?>[] arr = clz.getInterfaces();
			if (arr.length == 0) {
				beanListenInterfaces = new Class[] { BeanFieldListen.class };
			} else {
				beanListenInterfaces = new Class[arr.length + 1];
				System.arraycopy(arr, 0, beanListenInterfaces, 0, arr.length);
				beanListenInterfaces[arr.length] = BeanFieldListen.class;
			}
		}

		Enhancer enhancer = new Enhancer();
		enhancer.setInterfaces(beanListenInterfaces);
		if (classInfo.getSerialVersionUID() != null) {
			enhancer.setSerialVersionUID(classInfo.getSerialVersionUID());
		}

		enhancer.setCallbackType(FieldListenMethodInterceptor.class);
		enhancer.setSuperclass(clz);
		return enhancer.createClass();
	}

	/**
	 * 可以监听属性变化
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newFieldListenInstance(Class<T> clz) {
		return (T) createFieldListenEnhancer(clz).create();
	}

	/**
	 * 重新监听
	 * 
	 * @param bean
	 * @return
	 */
	public static <T> T restartFieldLinsten(T bean) {
		if (bean == null) {
			return bean;
		}

		if (bean instanceof BeanFieldListen) {
			((BeanFieldListen) bean).start_field_listen();
		}
		return bean;
	}

	/**
	 * 把一个普通对象转成可以监听字段变化的对象
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T transformationFieldListen(T bean) {
		ClassInfo classInfo = ClassUtils.getClassInfo(bean.getClass());
		BeanFieldListen proxy = (BeanFieldListen) newFieldListenInstance(classInfo.getClz());
		for (Entry<String, FieldInfo> entry : classInfo.getFieldMap().entrySet()) {
			FieldInfo fieldInfo = entry.getValue();
			if (fieldInfo.isStatic()) {
				continue;
			}

			Object v;
			try {
				v = fieldInfo.forceGet(bean);
				if (v != null) {
					fieldInfo.forceSet(proxy, v);
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		proxy.start_field_listen();
		return (T) proxy;
	}

	/**
	 * 获取主键数据
	 * @param bean
	 * @param tableInfo
	 * @param parse 是否转化为数据库类型的值
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Object[] getPrimaryKeys(Object bean, TableInfo tableInfo, boolean parse) throws IllegalArgumentException, IllegalAccessException {
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
