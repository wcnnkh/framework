package scw.aop.support;

import scw.aop.MethodInterceptor;
import scw.core.reflect.MethodInvoker;
import scw.mapper.Copy;
import scw.mapper.Field;
import scw.mapper.FieldFeature;
import scw.mapper.MapperUtils;

public class FieldSetterListenMethodInterceptor extends FieldSetterListenImpl implements MethodInterceptor {
	private static final long serialVersionUID = 1L;

	private Field getField(Class<?> clazz, String name, Class<?> type) {
		return MapperUtils.getFields(clazz).entity().all().accept(FieldFeature.SUPPORT_GETTER).find(name, type);
	}

	private final Object change(MethodInvoker invoker, Object[] args, Field field) throws Throwable {
		Object oldValue = field.getGetter().get(invoker.getInstance());
		if (FieldSetterListen.class.isAssignableFrom(invoker.getDeclaringClass())) {
			((FieldSetterListen) invoker.getInstance())._fieldSet(invoker, field, oldValue);
		} else {
			_fieldSet(invoker, field, oldValue);
		}
		return invoker.invoke(args);
	}

	public Object intercept(MethodInvoker invoker, Object[] args) throws Throwable {
		if (ProxyUtils.isWriteReplaceMethod(invoker, false)) {
			return Copy.copy(invoker.getDeclaringClass(), invoker.getInstance());
		}

		if (args.length == 0) {
			if (FieldSetterListen.CLEAR_FIELD_LISTEN.equals(invoker.getMethod().getName())) {
				if (FieldSetterListen.class.isAssignableFrom(invoker.getDeclaringClass())) {
					return invoker.invoke(args);
				} else {
					_clearFieldSetterMap();
					return null;
				}
			} else if (FieldSetterListen.GET_CHANGE_MAP.equals(invoker.getMethod().getName())) {
				if (FieldSetterListen.class.isAssignableFrom(invoker.getDeclaringClass())) {
					return invoker.invoke(args);
				} else {
					return _getFieldSetterMap();
				}
			}
		}

		FieldSetter fieldSetter = invoker.getMethod().getAnnotation(FieldSetter.class);
		if (fieldSetter != null) {
			Field field = getField(invoker.getDeclaringClass(), fieldSetter.value(), null);
			if (field != null) {
				return change(invoker, args, field);
			}
		} else if (args.length == 1 && invoker.getMethod().getName().startsWith("set")) {
			char[] chars = new char[invoker.getMethod().getName().length() - 3];
			chars[0] = Character.toLowerCase(invoker.getMethod().getName().charAt(3));
			invoker.getMethod().getName().getChars(4, invoker.getMethod().getName().length(), chars, 1);
			Class<?> type = invoker.getMethod().getParameterTypes()[0];
			Field field = getField(invoker.getDeclaringClass(), new String(chars), type);
			if (field == null) {
				if (type == boolean.class) {
					chars[0] = Character.toUpperCase(chars[0]);
					field = getField(invoker.getDeclaringClass(), "is" + new String(chars), boolean.class);
					if (field != null) {
						return change(invoker, args, field);
					}
				}
			} else {
				return change(invoker, args, field);
			}
		}
		return invoker.invoke(args);
	}
}
