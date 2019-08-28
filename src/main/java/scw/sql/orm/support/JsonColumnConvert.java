package scw.sql.orm.support;

import java.lang.reflect.Field;

import scw.json.JSONUtils;
import scw.sql.orm.DefaultColumnConvert;

public class JsonColumnConvert extends DefaultColumnConvert{

	public void setter(Field field, Object bean, Object value) throws Exception {
		if (value == null) {
			return;
		}

		if (value instanceof String) {
			Object obj = JSONUtils.parseObject((String) value, field.getGenericType());
			if (obj == null) {
				return;
			}

			field.set(bean, obj);
		}
	}

	public Object toSqlField(Field field, Object value) throws Exception {
		if(value == null){
			return null;
		}
		
		return JSONUtils.toJSONString(value);
	}
}
