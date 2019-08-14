package scw.sql.orm.support;

import java.lang.reflect.Field;

import scw.json.JSONUtils;
import scw.sql.orm.ColumnConvert;

public class JsonColumnConvert implements ColumnConvert {

	public Object getter(Field field, Object bean) throws Exception {
		Object value = field.get(bean);
		if (value == null) {
			return null;
		}

		return JSONUtils.toJSONString(value);
	}

	public void setter(Field field, Object bean, Object value) throws Exception {
		if (value == null) {
			return;
		}

		if (value instanceof String) {
			Object obj = JSONUtils.parseObject((String) value, field.getType());
			if (obj == null) {
				return;
			}

			field.set(bean, obj);
			;
		}
	}

}
