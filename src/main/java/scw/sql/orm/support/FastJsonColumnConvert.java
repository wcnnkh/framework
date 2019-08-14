package scw.sql.orm.support;

import java.lang.reflect.Field;

import scw.sql.orm.ColumnConvert;

import com.alibaba.fastjson.JSON;

public class FastJsonColumnConvert implements ColumnConvert{

	public Object getter(Field field, Object bean) throws Exception {
		Object value = field.get(bean);
		if(value == null){
			return null;
		}

		return JSON.toJSONString(value);
	}

	public void setter(Field field, Object bean, Object value) throws Exception {
		if(value == null){
			return ;
		}
		
		if(value instanceof String){
			Object obj = JSON.parseObject((String)value, field.getType());
			if(obj == null){
				return ;
			}
			
			field.set(bean, obj);
		}
		
	}

}
