package scw.sql.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.core.logger.Logger;
import scw.core.logger.LoggerFactory;
import scw.core.utils.ClassUtils;
import scw.sql.orm.annotation.UpdateField;

public final class TableFieldListenInterceptor implements MethodInterceptor, TableFieldListen {
	private static Logger logger = LoggerFactory.getLogger(TableFieldListenInterceptor.class);
	private static final long serialVersionUID = 1L;
	private transient Map<String, Object> field_change_map;
	private transient TableInfo tableInfo;

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (tableInfo == null) {
			tableInfo = ORMUtils.getTableInfo(obj.getClass());
		}

		if (args.length == 0) {
			if (TableFieldListen.CLEAR_FIELD_LISTEN.equals(method.getName())) {
				if (TableFieldListen.class.isAssignableFrom(tableInfo.getSource())) {
					return proxy.invokeSuper(obj, args);
				} else {
					clear_field_listen();
					return null;
				}
			} else if (TableFieldListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (TableFieldListen.class.isAssignableFrom(tableInfo.getSource())) {
					return proxy.invokeSuper(obj, args);
				} else {
					return get_field_change_map();
				}
			}
		}

		UpdateField updateField = method.getAnnotation(UpdateField.class);
		if (updateField != null) {
			ColumnInfo columnInfo = tableInfo.getColumnInfo(updateField.value());
			if (columnInfo == null) {
				logger.warn("指明的更新不存在,class={},field={}", tableInfo.getSource().getName(), updateField.value());
			} else {
				return change(obj, method, args, proxy, columnInfo.getField());
			}
		} else if (args.length == 1 && method.getName().startsWith("set")) {
			char[] chars = new char[method.getName().length() - 3];
			chars[0] = Character.toLowerCase(method.getName().charAt(3));
			method.getName().getChars(4, method.getName().length(), chars, 1);
			String fieldName = new String(chars);
			ColumnInfo columnInfo = tableInfo.getColumnInfo(fieldName);
			if (columnInfo == null) {
				chars[0] = Character.toUpperCase(chars[0]);
				columnInfo = tableInfo.getColumnInfo("is" + new String(chars));
				if (columnInfo != null && columnInfo.isDataBaseType()
						&& ClassUtils.isBooleanType(columnInfo.getType())) {
					return change(obj, method, args, proxy, columnInfo.getField());
				}
			} else {
				if (columnInfo.isDataBaseType()) {
					return change(obj, method, args, proxy, columnInfo.getField());
				}
			}
		}

		return proxy.invokeSuper(obj, args);
	}

	private final Object change(Object obj, Method method, Object[] args, MethodProxy proxy, Field field)
			throws Throwable {
		Object rtn;
		Object oldValue = null;
		oldValue = field.get(obj);
		rtn = proxy.invokeSuper(obj, args);
		if (TableFieldListen.class.isAssignableFrom(tableInfo.getSource())) {
			((TableFieldListen) obj).field_change(field, oldValue);
		} else {
			field_change(field, oldValue);
		}
		return rtn;
	}

	public Map<String, Object> get_field_change_map() {
		return field_change_map;
	}

	public void field_change(Field field, Object oldValue) {
		if (field_change_map == null) {
			field_change_map = new HashMap<String, Object>(tableInfo.getNotPrimaryKeyColumns().length, 1);
		}

		if (field_change_map.containsKey(field.getName())) {
			return;
		}

		field_change_map.put(field.getName(), oldValue);
	}

	public void clear_field_listen() {
		field_change_map = null;
	}
}
