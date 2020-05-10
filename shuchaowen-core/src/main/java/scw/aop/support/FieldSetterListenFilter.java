package scw.aop.support;

import scw.aop.FilterChain;
import scw.aop.Invoker;
import scw.aop.ProxyContext;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;

public class FieldSetterListenFilter extends FieldSetterListenImpl implements FilterChain {
	private static final long serialVersionUID = 1L;
	
	private Field getField(Class<?> clazz, String name, Class<?> type){
		return MapperUtils.getMapper().getField(clazz, name, type, FilterFeature.SUPPORT_GETTER, FilterFeature.IGNORE_STATIC);
	}
	
	private final Object change(Invoker invoker, ProxyContext context, Field field) throws Throwable {
		Object oldValue = field.getGetter().get(context.getProxy());
		if (FieldSetterListen.class.isAssignableFrom(context.getTargetClass())) {
			((FieldSetterListen) context.getProxy()).field_setter(context, field, oldValue);
		} else {
			field_setter(context, field, oldValue);
		}
		return invoker.invoke(context.getArgs());
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
}
