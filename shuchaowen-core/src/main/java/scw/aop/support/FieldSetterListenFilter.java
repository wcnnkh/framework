package scw.aop.support;

import java.io.ObjectStreamException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.core.reflect.ReflectionUtils;
import scw.core.utils.TypeUtils;

public class FieldSetterListenFilter implements FilterChain, FieldSetterListen {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> field_setter_map;

	protected boolean checkField(Field field) {
		if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())
				|| Modifier.isFinal(field.getModifiers())) {
			return false;
		}
		return true;
	}

	private final Object change(Invoker invoker, ProxyContext context, Field field) throws Throwable {
		Object rtn;
		Object oldValue = null;
		oldValue = field.get(context.getProxy());
		rtn = invoker.invoke(context.getArgs());
		if (FieldSetterListen.class.isAssignableFrom(context.getTargetClass())) {
			((FieldSetterListen) context.getProxy()).field_setter(context, field, oldValue);
		} else {
			field_setter(context, field, oldValue);
		}
		return rtn;
	}

	public Map<String, Object> get_field_setter_map() {
		return field_setter_map;
	}

	public void field_setter(ProxyContext context, Field field, Object oldValue) {
		if (field_setter_map == null) {
			field_setter_map = new LinkedHashMap<String, Object>(8);
		}

		if (field_setter_map.containsKey(field.getName())) {
			return;
		}

		field_setter_map.put(field.getName(), oldValue);
	}

	public void clear_field_setter_listen() {
		field_setter_map = null;
	}

	public Object doFilter(Invoker invoker, ProxyContext context) throws Throwable {
		if (context.getArgs().length == 0) {
			if (FieldSetterListen.CLEAR_FIELD_LISTEN.equals(context.getMethod().getName())) {
				if (FieldSetterListen.class.isAssignableFrom(context.getTargetClass())) {
					return invoker.invoke(context.getArgs());
				} else {
					clear_field_setter_listen();
					return null;
				}
			} else if (FieldSetterListen.GET_CHANGE_MAP.equals(context.getMethod().getName())) {
				if (FieldSetterListen.class.isAssignableFrom(context.getTargetClass())) {
					return invoker.invoke(context.getArgs());
				} else {
					return get_field_setter_map();
				}
			}
		}

		FieldSetter fieldSetter = context.getMethod().getAnnotation(FieldSetter.class);
		if (fieldSetter != null) {
			Field field = ReflectionUtils.getField(context.getTargetClass(), fieldSetter.value(), true);
			if (field != null && checkField(field)) {
				return change(invoker, context, field);
			}
		} else if (context.getArgs().length == 1 && context.getMethod().getName().startsWith("set")) {
			char[] chars = new char[context.getMethod().getName().length() - 3];
			chars[0] = Character.toLowerCase(context.getMethod().getName().charAt(3));
			context.getMethod().getName().getChars(4, context.getMethod().getName().length(), chars, 1);
			Field field = ReflectionUtils.getField(context.getTargetClass(), new String(chars), true);
			if (field == null) {
				chars[0] = Character.toUpperCase(chars[0]);
				field = ReflectionUtils.getField(context.getTargetClass(), "is" + new String(chars), true);
				if (field != null && TypeUtils.isBoolean(field.getType()) && checkField(field)) {
					return change(invoker, context, field);
				}
			} else if (checkField(field)) {
				return change(invoker, context, field);
			}
		}
		return invoker.invoke(context.getArgs());
	}

	public Object writeReplace() throws ObjectStreamException {
		return this;
	}
}
