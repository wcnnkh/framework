package scw.sql.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public final class TableFieldListenInterceptor implements MethodInterceptor,
		TableFieldListen {
	private static final long serialVersionUID = 1L;
	private transient Map<String, Object> field_change_map;
	private transient TableInfo tableInfo;

	public Object intercept(Object obj, Method method, Object[] args,
			MethodProxy proxy) throws Throwable {
		if (tableInfo == null) {
			tableInfo = ORMUtils.getTableInfo(obj.getClass());
		}

		if (args.length == 0) {
			if (TableFieldListen.CLEAR_FIELD_LISTEN.equals(method.getName())) {
				if (TableFieldListen.class.isAssignableFrom(tableInfo
						.getSource())) {
					return proxy.invokeSuper(obj, args);
				} else {
					clear_field_listen();
					return null;
				}
			} else if (TableFieldListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (TableFieldListen.class.isAssignableFrom(tableInfo
						.getSource())) {
					return proxy.invokeSuper(obj, args);
				} else {
					return get_field_change_map();
				}
			}
		}

		if (args.length == 1) {
			Field field = tableInfo.getFieldInfoBySetterName(method.getName());
			if (field != null) {
				Object rtn;
				Object oldValue = null;
				oldValue = field.get(obj);
				rtn = proxy.invokeSuper(obj, args);
				if (TableFieldListen.class.isAssignableFrom(tableInfo
						.getSource())) {
					((TableFieldListen) obj).field_change(field, oldValue);
				} else {
					field_change(field, oldValue);
				}
				return rtn;
			}
		}

		return proxy.invokeSuper(obj, args);
	}

	public Map<String, Object> get_field_change_map() {
		return field_change_map;
	}

	public void field_change(Field field, Object oldValue) {
		if (field_change_map == null) {
			field_change_map = new HashMap<String, Object>(
					tableInfo.getNotPrimaryKeyColumns().length, 1);
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
