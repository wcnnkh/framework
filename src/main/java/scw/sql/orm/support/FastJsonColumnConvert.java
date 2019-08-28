package scw.sql.orm.support;

import java.lang.reflect.Field;

import com.alibaba.fastjson.JSON;

import scw.sql.orm.DefaultColumnConvert;

public class FastJsonColumnConvert extends DefaultColumnConvert {

	public void setter(Field field, Object bean, Object value) throws Exception {
		if (value == null) {
			return;
		}

		if (value instanceof String) {
			Object obj = JSON.parseObject((String) value, field.getGenericType());
			if (obj == null) {
				return;
			}

			field.set(bean, obj);
		}

	}

	public Object toSqlField(Field field, Object value) throws Exception {
		if (value == null) {
			return null;
		}

		return JSON.toJSONString(value);
	}
}
