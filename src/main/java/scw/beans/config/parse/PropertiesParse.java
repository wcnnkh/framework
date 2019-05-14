package scw.beans.config.parse;

import java.io.File;
import java.util.Properties;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.core.ClassInfo;
import scw.core.FieldInfo;
import scw.core.utils.ClassUtils;
import scw.core.utils.ConfigUtils;
import scw.core.utils.StringParseUtils;

public final class PropertiesParse implements ConfigParse {
	public Object parse(BeanFactory beanFactory, FieldInfo field, String filePath, String charset) throws Exception{
		File file = ConfigUtils.getFile(filePath);
		Properties properties = ConfigUtils.getProperties(file, charset);
		if(ClassUtils.isPrimitiveOrWrapper(field.getType()) || ClassUtils.isStringType(field.getType())){
			String v = ConfigUtils.format(properties.getProperty(field.getName()));
			return StringParseUtils.conversion(v, field.getType());
		}else if(Properties.class.isAssignableFrom(field.getType())){
			return properties;
		}else{
			Class<?> fieldTpe = field.getType();
			ClassInfo fieldClassInfo = ClassUtils.getClassInfo(fieldTpe);
			try {
				Object obj = fieldTpe.newInstance();
				for (Object key : properties.keySet()) {
					FieldInfo fieldInfo = fieldClassInfo.getFieldInfo(key.toString(), false);
					if(fieldInfo == null){
						continue;
					}
					
					String value = ConfigUtils.format(properties.getProperty(key.toString()));
					fieldInfo.set(obj, StringParseUtils.conversion(value, fieldInfo.getType()));
				}
				return obj;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}