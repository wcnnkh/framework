package scw.orm.sql.support;

import java.lang.reflect.Field;

import scw.core.utils.EnumUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.TypeUtils;
import scw.json.JSONUtils;
import scw.orm.Filter;
import scw.orm.Getter;
import scw.orm.GetterFilterChain;
import scw.orm.MappingContext;
import scw.orm.Setter;
import scw.orm.SetterFilterChain;

public class DefaultSqlFilter implements Filter {
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

		if (SqlORMUtils.isDataBaseType(field.getType())) {
			return value;
		} else {
			if (value == null) {
				return null;
			}

			return JSONUtils.toJSONString(value);
		}
	}

	public Object getter(MappingContext context, Getter getter, GetterFilterChain chain) throws Exception {
		return toSqlField(context.getFieldDefinition().getField(), getter.getter(context));
	}

	public void setter(MappingContext context, Setter setter, Object value, SetterFilterChain chain) throws Exception {
		if (value == null) {
			return;
		}

		Field field = context.getFieldDefinition().getField();
		Class<?> type = field.getType();
		if (type.isInstance(value)) {
			setter.setter(context, value);
			return;
		}

		if (TypeUtils.isBoolean(type)) {
			if (value != null) {
				if (value instanceof Number) {
					setter.setter(context, ((Number) value).intValue() == 1);
				} else {
					setter.setter(context, StringUtils.parseBoolean(value.toString()));
				}
			}
		} else if (TypeUtils.isInt(type)) {
			if (value instanceof Number) {
				setter.setter(context, ((Number) value).intValue());
			} else {
				setter.setter(context, StringUtils.parseInt(value.toString()));
			}
		} else if (TypeUtils.isLong(type)) {
			if (value instanceof Number) {
				setter.setter(context, ((Number) value).longValue());
			} else {
				setter.setter(context, StringUtils.parseLong(value.toString()));
			}
		} else if (TypeUtils.isByte(type)) {
			if (value instanceof Number) {
				setter.setter(context, ((Number) value).byteValue());
			} else {
				setter.setter(context, StringUtils.parseByte(value.toString()));
			}
		} else if (TypeUtils.isFloat(field.getType())) {
			if (value instanceof Number) {
				setter.setter(context, ((Number) value).floatValue());
			} else {
				setter.setter(context, StringUtils.parseFloat(value.toString()));
			}
		} else if (TypeUtils.isDouble(field.getType())) {
			if (value instanceof Number) {
				setter.setter(context, ((Number) value).doubleValue());
			} else {
				setter.setter(context, StringUtils.parseDouble(value.toString()));
			}
		} else if (TypeUtils.isShort(field.getType())) {
			if (value instanceof Number) {
				setter.setter(context, ((Number) value).shortValue());
			} else {
				setter.setter(context, StringUtils.parseShort(value.toString()));
			}
		} else if (type.isEnum()) {
			setter.setter(context, EnumUtils.valueOf(type, value.toString()));
		} else {
			Object obj = JSONUtils.parseObject(value.toString(), field.getGenericType());
			if (obj == null) {
				return;
			}
			setter.setter(context, obj);
		}
	}
}
