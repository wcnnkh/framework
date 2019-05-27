package scw.core;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scw.core.reflect.ReflectUtils;
import scw.core.utils.ClassUtils;

public class FieldSetterListenInterceptor implements MethodInterceptor, FieldSetterListen, Serializable {
	private static final long serialVersionUID = 1L;
	private transient Map<String, Object> field_setter_map;
	private transient Class<?> source;

	public final Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (source == null) {
			source = ClassUtils.getUserClass(obj);
		}
		
		if (args.length == 0) {
			if (FieldSetterListen.CLEAR_FIELD_LISTEN.equals(method.getName())) {
				if (FieldSetterListen.class.isAssignableFrom(source)) {
					return proxy.invokeSuper(obj, args);
				} else {
					clear_field_setter_listen();
					return null;
				}
			} else if (FieldSetterListen.GET_CHANGE_MAP.equals(method.getName())) {
				if (FieldSetterListen.class.isAssignableFrom(source)) {
					return proxy.invokeSuper(obj, args);
				} else {
					return get_field_setter_map();
				}
			}
		}

		FieldSetter fieldSetter = method.getAnnotation(FieldSetter.class);
		if (fieldSetter != null) {
			Field field = ReflectUtils.getFieldUseCache(source, fieldSetter.value(), true);
			if (field != null && checkField(field)) {
				return change(obj, method, args, proxy, field);
			}
		} else if (args.length == 1 && method.getName().startsWith("set")) {
			char[] chars = new char[method.getName().length() - 3];
			chars[0] = Character.toLowerCase(method.getName().charAt(3));
			method.getName().getChars(4, method.getName().length(), chars, 1);
			Field field = ReflectUtils.getFieldUseCache(source, new String(chars), true);
			if (field == null) {
				chars[0] = Character.toUpperCase(chars[0]);
				field = ReflectUtils.getFieldUseCache(source, "is" + new String(chars), true);
				if (field != null && ClassUtils.isBooleanType(field.getType()) && checkField(field)) {
					return change(obj, method, args, proxy, field);
				}
			} else if (checkField(field)) {
				return change(obj, method, args, proxy, field);
			}
		}

		return proxy.invokeSuper(obj, args);
	}

	protected boolean checkField(Field field) {
		if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())
				|| Modifier.isFinal(field.getModifiers())) {
			return false;
		}
		return true;
	}

	private final Object change(Object obj, Method method, Object[] args, MethodProxy proxy, Field field)
			throws Throwable {
		Object rtn;
		Object oldValue = null;
		oldValue = field.get(obj);
		rtn = proxy.invokeSuper(obj, args);
		if (FieldSetterListen.class.isAssignableFrom(source)) {
			((FieldSetterListen) obj).field_setter(obj, field, oldValue);
		} else {
			field_setter(obj, field, oldValue);
		}
		return rtn;
	}

	public Map<String, Object> get_field_setter_map() {
		return field_setter_map;
	}

	public void field_setter(Object bean, Field field, Object oldValue) {
		if (field_setter_map == null) {
			field_setter_map = new HashMap<String, Object>(8);
		}

		if (field_setter_map.containsKey(field.getName())) {
			return;
		}

		field_setter_map.put(field.getName(), oldValue);
	}

	public void clear_field_setter_listen() {
		field_setter_map = null;
	}
}
