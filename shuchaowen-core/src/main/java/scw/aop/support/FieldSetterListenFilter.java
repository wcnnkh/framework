package scw.aop.support;

import java.io.ObjectStreamException;
import java.util.LinkedHashMap;
import java.util.Map;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;

public class FieldSetterListenFilter implements FilterChain, FieldSetterListen {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> field_setter_map;

	private final Object change(Invoker invoker, ProxyContext context, Field field) throws Throwable {
		Object rtn;
		Object oldValue = null;
		oldValue = field.getGetter().get(context.getProxy());
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

		if (field_setter_map.containsKey(field.getGetter().getName())) {
			return;
		}

		field_setter_map.put(field.getGetter().getName(), oldValue);
	}

	public void clear_field_setter_listen() {
		field_setter_map = null;
	}
	
	private Field getField(Class<?> clazz, String name, Class<?> type){
		return MapperUtils.getMapper().getField(clazz, name, type, FilterFeature.SUPPORT_GETTER, FilterFeature.IGNORE_STATIC);
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
			Field field = getField(context.getTargetClass(), fieldSetter.value(), null);
			if (field != null) {
				return change(invoker, context, field);
			}
		} else if (context.getArgs().length == 1 && context.getMethod().getName().startsWith("set")) {
			char[] chars = new char[context.getMethod().getName().length() - 3];
			chars[0] = Character.toLowerCase(context.getMethod().getName().charAt(3));
			context.getMethod().getName().getChars(4, context.getMethod().getName().length(), chars, 1);
			Class<?> type = context.getMethod().getParameterTypes()[0];
			Field field = getField(context.getTargetClass(), new String(chars), type);
			if (field == null) {
				if(type == boolean.class){
					chars[0] = Character.toUpperCase(chars[0]);
					field = getField(context.getTargetClass(), "is" + new String(chars), boolean.class);
					if (field != null) {
						return change(invoker, context, field);
					}
				}
				
			} else {
				return change(invoker, context, field);
			}
		}
		return invoker.invoke(context.getArgs());
	}

	public Object writeReplace() throws ObjectStreamException {
		return this;
	}
}
