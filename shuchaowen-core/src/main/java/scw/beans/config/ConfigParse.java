package scw.beans.config;

import scw.beans.BeanFactory;
import scw.core.reflect.FieldDefinition;

/**
 * 解析配置文件
 * 
 * @author shuchaowen
 *
 */
public interface ConfigParse {
	Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset) throws Exception;
}
