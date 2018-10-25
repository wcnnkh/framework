package shuchaowen.core.beans.config.parse;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.beans.config.ConfigParse;
import shuchaowen.core.util.ClassUtils;
import shuchaowen.core.util.ConfigUtils;
import shuchaowen.core.util.FieldInfo;

public class XmlToBeanMapParse implements ConfigParse{
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		List<Map<String, String>> list = ConfigUtils.getDefaultXmlContent(file, "config");
		String type = field.getField().getGenericType().getTypeName();
		type = type.substring(type.indexOf("<") + 1, type.indexOf(">"));
		String valueType = type.split(",")[1].trim();
		try {
			Class<?> toClz = ClassUtils.forName(valueType);
			Field keyField = null;
			for(Field f :toClz.getDeclaredFields()){
				if(Modifier.isStatic(f.getModifiers())){
					continue;
				}
				
				keyField = f;
				break;
			}
			
			if(keyField == null){
				throw new NullPointerException("打不到主键字段");
			}
			
			Map<Object, Object> map = new HashMap<Object, Object>();
			for(Map<String, String> tempMap : list){
				Object obj = ConfigUtils.parseObject(tempMap, toClz);
				keyField.setAccessible(true);
				Object kV = keyField.get(obj);
				keyField.setAccessible(false);
				if(map.containsKey(kV)){
					throw new NullPointerException("已经存在的key="+keyField.getName()+",value=" + kV + ", filePath=" + file.getPath());
				}
				map.put(kV, obj);
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
