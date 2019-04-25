package scw.beans.config;

import scw.beans.BeanFactory;
import scw.core.FieldInfo;


/**
 * 解析配置文件
 * @author shuchaowen
 *
 */
public interface ConfigParse {
	Object parse(BeanFactory beanFactory, FieldInfo fieldInfo, String filePath, String charset) throws Exception;
}
