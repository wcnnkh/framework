package scw.aop.support;

import scw.aop.MethodInterceptor;
import scw.aop.MethodInterceptorChain;
import scw.aop.ProxyUtils;
import scw.core.reflect.MethodInvoker;
import scw.mapper.Copy;
import scw.mapper.Field;
import scw.mapper.FilterFeature;
import scw.mapper.MapperUtils;

public class FieldSetterListenMethodInterceptor extends FieldSetterListenImpl implements MethodInterceptor {
	private static final long serialVersionUID = 1L;

	private Field getField(Class<?> clazz, String name, Class<?> type) {
		return MapperUtils.getMapper().getFields(clazz, FilterFeature.SUPPORT_GETTER, FilterFeature.IGNORE_STATIC)
				.find(name, type);
	}

	private final Object change(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain, Field field)
			throws Throwable {
		Object oldValue = field.getGetter().get(invoker.getInstance());
		if (FieldSetterListen.class.isAssignableFrom(invoker.getSourceClass())) {
			((FieldSetterListen) invoker.getInstance()).field_setter(invoker, field, oldValue);
		} else {
			field_setter(invoker, field, oldValue);
		}
		return filterChain.intercept(invoker, args);
	}

	public Object intercept(MethodInvoker invoker, Object[] args, MethodInterceptorChain filterChain) throws Throwable {
		if (ProxyUtils.isWriteReplaceMethod(invoker, false)) {
			return Copy.copy(invoker.getSourceClass(), invoker.getInstance());
		}

		if (args.length == 0) {
			if (FieldSetterListen.CLEAR_FIELD_LISTEN.equals(invoker.getMethod().getName())) {
				if (FieldSetterListen.class.isAssignableFrom(invoker.getSourceClass())) {
					return filterChain.intercept(invoker, args);
				} else {
					clear_field_setter_listen();
					return null;
				}
			} else if (FieldSetterListen.GET_CHANGE_MAP.equals(invoker.getMethod().getName())) {
				if (FieldSetterListen.class.isAssignableFrom(invoker.getSourceClass())) {
					return filterChain.intercept(invoker, args);
				} else {
					return get_field_setter_map();
				}
			}
		}

		FieldSetter fieldSetter = invoker.getMethod().getAnnotation(FieldSetter.class);
		if (fieldSetter != null) {
			Field field = getField(invoker.getSourceClass(), fieldSetter.value(), null);
			if (field != null) {
				return change(invoker, args, filterChain, field);
			}
		} else if (args.length == 1 && invoker.getMethod().getName().startsWith("set")) {
			char[] chars = new char[invoker.getMethod().getName().length() - 3];
			chars[0] = Character.toLowerCase(invoker.getMethod().getName().charAt(3));
			invoker.getMethod().getName().getChars(4, invoker.getMethod().getName().length(), chars, 1);
			Class<?> type = invoker.getMethod().getParameterTypes()[0];
			Field field = getField(invoker.getSourceClass(), new String(chars), type);
			if (field == null) {
				if (type == boolean.class) {
					chars[0] = Character.toUpperCase(chars[0]);
					field = getField(invoker.getSourceClass(), "is" + new String(chars), boolean.class);
					if (field != null) {
						return change(invoker, args, filterChain, field);
					}
				}
			} else {
				return change(invoker, args, filterChain, field);
			}
		}
		return filterChain.intercept(invoker, args);
	}
}
