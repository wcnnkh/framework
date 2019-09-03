package scw.sql.orm;

import java.io.Reader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Time;
import java.sql.Timestamp;

import scw.core.utils.ClassUtils;
import scw.core.utils.EnumUtils;
import scw.core.utils.StringUtils;
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
		if (ClassUtils.isBooleanType(type)) {
			if (value != null) {
				if (value instanceof Number) {
					field.setBoolean(bean, ((Number) value).intValue() == 1);
				} else if (value instanceof String) {
					field.setBoolean(bean, StringUtils.parseBoolean((String) value));
				}
			}
		} else if (ClassUtils.isIntType(type)) {
			if (value instanceof Number) {
				field.setInt(bean, ((Number) value).intValue());
			} else if (value instanceof String) {
				field.setInt(bean, StringUtils.parseInt((String) value));
			}
		} else if (ClassUtils.isLongType(type)) {
			if (value instanceof Number) {
				field.setLong(bean, ((Number) value).longValue());
			} else if (value instanceof String) {
				field.setLong(bean, StringUtils.parseLong((String) value));
			}
		} else if (ClassUtils.isByteType(type)) {
			if (value instanceof Number) {
				field.setByte(bean, ((Number) value).byteValue());
			} else if (value instanceof String) {
				field.setByte(bean, StringUtils.parseByte((String) value));
			}
		} else if (ClassUtils.isFloatType(field.getType())) {
			if (value instanceof Number) {
				field.setFloat(bean, ((Number) value).floatValue());
			} else if (value instanceof String) {
				field.setFloat(bean, StringUtils.parseFloat((String) value));
			}
		} else if (ClassUtils.isDoubleType(field.getType())) {
			if (value instanceof Number) {
				field.setDouble(bean, ((Number) value).doubleValue());
			} else if (value instanceof String) {
				field.setDouble(bean, StringUtils.parseDouble((String) value));
			}
		} else if (ClassUtils.isShortType(field.getType())) {
			if (value instanceof Number) {
				field.setShort(bean, ((Number) value).shortValue());
			} else if (value instanceof String) {
				field.setShort(bean, StringUtils.parseShort((String) value));
			}
		} else if(type.isEnum()){
			field.set(bean, EnumUtils.valueOf(type, value.toString()));
		} else if (String.class.isAssignableFrom(type) || Date.class.isAssignableFrom(type)
				|| java.util.Date.class.isAssignableFrom(type) || Time.class.isAssignableFrom(type)
				|| Timestamp.class.isAssignableFrom(type) || Array.class.isAssignableFrom(type)
				|| Blob.class.isAssignableFrom(type) || Clob.class.isAssignableFrom(type)
				|| BigDecimal.class.isAssignableFrom(type) || Reader.class.isAssignableFrom(type)
				|| NClob.class.isAssignableFrom(type)) {
			field.set(bean, value);
		} else {
			Object obj = JSONUtils.parseObject(value.toString(), field.getGenericType());
			if (obj == null) {
				return;
			}
			field.set(bean, obj);
		}
	}
}
