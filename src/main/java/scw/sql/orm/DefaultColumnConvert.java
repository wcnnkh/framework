package scw.sql.orm;

import java.lang.reflect.Field;

import scw.core.utils.ClassUtils;
import scw.core.utils.EnumUtils;
import scw.core.utils.StringUtils;

public class DefaultColumnConvert implements ColumnConvert {

	public Object getter(Field field, Object bean) throws Exception {
		Object value = field.get(bean);
		if (field.getType().isEnum()) {
			return bean == null ? null : bean.toString();
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
		return value;
	}

	public void setter(Field field, Object bean, Object value) throws Exception {
		if(value == null){
			return ;
		}
		
		if(field.getType().isEnum()){
			field.set(bean, EnumUtils.valueOf(field.getType(), value.toString()));
			return ;
		}
		
		field.set(bean, parse(field.getType(), value));
	}

	/**
	 * 将数据库值转化java类型
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public Object parse(Class<?> type, Object value) {
		if (value == null) {
			return value;
		}

		if (ClassUtils.isBooleanType(type)) {
			if (value != null) {
				if (value instanceof Number) {
					return ((Number) value).intValue() == 1;
				} else if (value instanceof String) {
					return StringUtils.parseBoolean((String) value);
				}
			}
		} else if (ClassUtils.isIntType(type)) {
			if (value instanceof Number) {
				return ((Number) value).intValue();
			}
		} else if (ClassUtils.isLongType(type)) {
			if (value instanceof Number) {
				return ((Number) value).longValue();
			}
		} else if (ClassUtils.isByteType(type)) {
			if (value instanceof Number) {
				return ((Number) value).byteValue();
			}
		} else if (ClassUtils.isFloatType(type)) {
			if (value instanceof Number) {
				return ((Number) value).floatValue();
			}
		} else if (ClassUtils.isDoubleType(type)) {
			if (value instanceof Number) {
				return ((Number) value).doubleValue();
			}
		} else if (ClassUtils.isShortType(type)) {
			if (value instanceof Number) {
				return ((Number) value).shortValue();
			}
		}
		return value;
	}
}
