package scw.sql.orm;

import java.lang.reflect.Field;

import scw.core.utils.EnumUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.json.JSONUtils;

public class DefaultColumnConvert implements ColumnConvert {

	public Object getter(Field field, Object bean) throws Exception {
		return toSqlField(field, field.get(bean));
	}

	public Object toSqlField(Field field, Object value) throws Exception {
		if (field.getType().isEnum()) {
			return value == null ? null : value.toString();
		}

		if (boolean.class == field.getType()) {
			boolean b = value == null ? false : (Boolean) value;
			return b ? 1 : 0;
		}

		if (Boolean.class == field.getType()) {
			if (value == null) {
				return null;
			}
			return (Boolean) value ? 1 : 0;
		}

		if (ORMUtils.isDataBaseType(field.getType())) {
			return value;
		} else {
			if (value == null) {
				return null;
			}

			return JSONUtils.toJSONString(value);
		}
	}

	public void setter(Field field, Object bean, Object value) throws Exception {
		if (value == null) {
			return;
		}

		Class<?> type = field.getType();
		if (type.isInstance(value)) {
			field.set(bean, value);
			return;
		}

		if (TypeUtils.isBoolean(type)) {
			if (value != null) {
				if (value instanceof Number) {
					field.set(bean, ((Number) value).intValue() == 1);
				} else {
					field.set(bean, StringUtils.parseBoolean(value.toString()));
				}
			}
		} else if (TypeUtils.isInt(type)) {
			if (value instanceof Number) {
				field.set(bean, ((Number) value).intValue());
			} else {
				field.set(bean, StringUtils.parseInt(value.toString()));
			}
		} else if (TypeUtils.isLong(type)) {
			if (value instanceof Number) {
				field.set(bean, ((Number) value).longValue());
			} else {
				field.set(bean, StringUtils.parseLong(value.toString()));
			}
		} else if (TypeUtils.isByte(type)) {
			if (value instanceof Number) {
				field.set(bean, ((Number) value).byteValue());
			} else {
				field.set(bean, StringUtils.parseByte(value.toString()));
			}
		} else if (TypeUtils.isFloat(field.getType())) {
			if (value instanceof Number) {
				field.set(bean, ((Number) value).floatValue());
			} else {
				field.set(bean, StringUtils.parseFloat(value.toString()));
			}
		} else if (TypeUtils.isDouble(field.getType())) {
			if (value instanceof Number) {
				field.set(bean, ((Number) value).doubleValue());
			} else {
				field.set(bean, StringUtils.parseDouble(value.toString()));
			}
		} else if (TypeUtils.isShort(field.getType())) {
			if (value instanceof Number) {
				field.set(bean, ((Number) value).shortValue());
			} else {
				field.set(bean, StringUtils.parseShort(value.toString()));
			}
		} else if (type.isEnum()) {
			field.set(bean, EnumUtils.valueOf(type, value.toString()));
		} else {
			Object obj = JSONUtils.parseObject(value.toString(), field.getGenericType());
			if (obj == null) {
				return;
			}
			field.set(bean, obj);
		}
	}
}
