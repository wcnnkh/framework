package shuchaowen.common.utils;

import java.util.Map.Entry;
import java.util.Properties;

import shuchaowen.common.ClassInfo;
import shuchaowen.common.FieldInfo;

public final class PropertiesUtils {
	private PropertiesUtils(){};
	
	public static <T> T setProperties(Object obj, Properties properties, StringFormat stringFormat){
		T t = null;
		ClassInfo classInfo = ClassUtils.getClassInfo(obj.getClass());
		FieldInfo fieldInfo;
		try {
			for(Entry<Object, Object> entry : properties.entrySet()){
				String key = stringFormat.format(entry.getKey().toString());
				fieldInfo = classInfo.getFieldMap().get(key);
				if(fieldInfo != null){
					String value = entry.getValue() == null? null:entry.getValue().toString();
					fieldInfo.set(obj, stringFormat.format(value));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			t = null;
		}
		return t;
	}
	
	public static Object getValue(Class<?> type, Object value) throws Exception{
		if(value == null){
			return null;
		}
		
		String v = value.toString();
		if(String.class.isAssignableFrom(type)){
			return v;
		}else if(int.class.isAssignableFrom(type)){
			return Integer.parseInt(v);
		}else if(Integer.class.isAssignableFrom(type)){
			return Integer.valueOf(v);
		}else if(long.class.isAssignableFrom(type)){
			return Long.parseLong(v);
		}else if(Long.class.isAssignableFrom(type)){
			return Long.valueOf(v);
		}else if(float.class.isAssignableFrom(type)){
			return Float.parseFloat(v);
		}else if(Float.class.isAssignableFrom(type)){
			return Float.valueOf(v);
		}else if(double.class.isAssignableFrom(type)){
			return Double.parseDouble(v);
		}else if(Double.class.isAssignableFrom(type)){
			return Double.valueOf(v);
		}else if(boolean.class.isAssignableFrom(type)){
			return "1".equals(v)? true:Boolean.parseBoolean(v);
		}else if(Boolean.class.isAssignableFrom(type)){
			return "1".equals(v)? true:Boolean.valueOf(v);
		}else if(byte.class.isAssignableFrom(type)){
			return Byte.parseByte(v);
		}else if(Byte.class.isAssignableFrom(type)){
			return Byte.valueOf(v);
		}else if(short.class.isAssignableFrom(type)){
			return Short.parseShort(v);
		}else if(Short.class.isAssignableFrom(type)){
			return Short.valueOf(v);
		}else if(char.class.isAssignableFrom(type)){
			return v;
		}else if(Character.class.isAssignableFrom(type)){
			return new Character(v.charAt(0));
		}else{
			return value;
		}
	}
}
