package scw.beans.config;

import scw.beans.BeanFactory;
import scw.core.reflect.FieldDefinition;
import scw.util.value.property.PropertyFactory;

/**
 * 解析配置文件
 * 
 * @author shuchaowen
 *
 */
public interface ConfigParse {
	Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition fieldDefinition, String filePath, String charset) throws Exception;
}
