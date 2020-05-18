package scw.beans.config;

import scw.beans.BeanFactory;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

/**
 * 解析配置文件
 * 
 * @author shuchaowen
 *
 */
public interface ConfigParse {
	Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String filePath, String charset) throws Exception;
}
