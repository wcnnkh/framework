package scw.sql.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public final class FieldListenMethodInterceptor implements MethodInterceptor, BeanFieldListen {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> changeColumnMap;
	private boolean startListen = false;
	private transient TableInfo tableInfo;

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (tableInfo == null) {
			tableInfo = ORMUtils.getTableInfo(obj.getClass());
		}

		if (args.length == 0) {
			if (BeanFieldListen.START_LISTEN.equals(method.getName())) {
				if (BeanFieldListen.class.isAssignableFrom(tableInfo.getSource())) {
					startListen = true;
					return proxy.invokeSuper(obj, args);
				} else {
					start_field_listen();
					return null;
				}
			} else if (BeanFieldListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (BeanFieldListen.class.isAssignableFrom(tableInfo.getSource())) {
					return proxy.invokeSuper(obj, args);
				} else {
					return get_field_change_map();
				}
			}
		}

		if (startListen) {
			Field field = tableInfo.getFieldInfoBySetterName(method.getName());
			if (field != null) {
				Object rtn;
				Object oldValue = null;
				oldValue = field.get(obj);
				rtn = proxy.invokeSuper(obj, args);
				if (BeanFieldListen.class.isAssignableFrom(tableInfo.getSource())) {
					((BeanFieldListen) obj).field_change(field, oldValue);
				} else {
					field_change(field, oldValue);
				}
				return rtn;
			}
		}
		return proxy.invokeSuper(obj, args);
	}

	public Map<String, Object> get_field_change_map() {
		return changeColumnMap;
	}

	public void start_field_listen() {
		if (changeColumnMap != null && !changeColumnMap.isEmpty()) {
			changeColumnMap.clear();
		}
		startListen = true;
	}

	public void field_change(Field field, Object oldValue) {
		if (changeColumnMap == null) {
			changeColumnMap = new HashMap<String, Object>();
		}
		changeColumnMap.put(field.getName(), oldValue);
	}
}
