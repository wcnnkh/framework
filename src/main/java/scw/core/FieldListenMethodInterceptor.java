package scw.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.core.utils.ClassUtils;

public final class FieldListenMethodInterceptor implements MethodInterceptor, BeanFieldListen {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> changeColumnMap;
	private boolean startListen = false;
	private transient ClassInfo classInfo;

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (classInfo == null) {
			classInfo = ClassUtils.getClassInfo(obj.getClass());
		}

		if (args.length == 0) {
			if (BeanFieldListen.START_LISTEN.equals(method.getName())) {
				if (BeanFieldListen.class.isAssignableFrom(classInfo.getSource())) {
					startListen = true;
					return proxy.invokeSuper(obj, args);
				} else {
					start_field_listen();
					return null;
				}
			} else if (BeanFieldListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (BeanFieldListen.class.isAssignableFrom(classInfo.getSource())) {
					return proxy.invokeSuper(obj, args);
				} else {
					return get_field_change_map();
				}
			}
		}

		if (startListen) {
			FieldInfo fieldInfo = classInfo.getFieldInfoBySetterName(method.getName(), true);
			if (fieldInfo != null) {
				Object rtn;
				Object oldValue = null;
				oldValue = fieldInfo.forceGet(obj);
				rtn = proxy.invokeSuper(obj, args);
				if (BeanFieldListen.class.isAssignableFrom(classInfo.getSource())) {
					((BeanFieldListen) obj).field_change(fieldInfo, oldValue);
				} else {
					field_change(fieldInfo, oldValue);
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

	public void field_change(FieldInfo fieldInfo, Object oldValue) {
		if (changeColumnMap == null) {
			changeColumnMap = new HashMap<String, Object>();
		}
		changeColumnMap.put(fieldInfo.getName(), oldValue);
	}
}
