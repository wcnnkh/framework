package scw.sql.orm.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import scw.core.utils.ArrayUtils;
import scw.core.utils.EnumUtils;
import scw.sql.orm.ColumnConvert;

/**
 * 枚举支持
 * @author shuchaowen
 *
 */
public class EnumColumnConvert implements ColumnConvert {
	public Object getter(Field field, Object bean) throws Exception {
		Object v = field.get(bean);
		Field enumField = getField(field.getType());
		if (enumField == null) {// 直接使用值做为枚举
			return v == null ? null : v.toString();
		}

		return enumField.get(v);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setter(Field field, Object bean, Object value) throws Exception {
		if (value == null) {
			return;
		}

		Class<? extends Enum> enumType = ((Class<? extends Enum>) field.getType());
		Field enumField = getField(field.getType());
		Enum e;
		if (enumField == null) {// 直接使用值做为枚举
			e = EnumUtils.valueOf(enumType, value.toString());
		} else {
			e = EnumUtils.valueOf(enumType, enumField, value);
		}
		field.set(bean, e);
	}

	private static Field getField(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		if (ArrayUtils.isEmpty(fields)) {
			return null;
		}

		Field f = null;
		for (Field field : fields) {
			EnumIdField enumIdField = field.getAnnotation(EnumIdField.class);
			if (enumIdField != null) {
				f = field;
				break;
			}
		}
		return f;
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface EnumIdField {
	}
}
