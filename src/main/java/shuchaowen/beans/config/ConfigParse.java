package shuchaowen.beans.config;

import shuchaowen.beans.BeanFactory;
import shuchaowen.common.FieldInfo;


/**
 * 解析配置文件
 * @author shuchaowen
 *
 */
public interface ConfigParse {
	Object parse(BeanFactory beanFactory, FieldInfo fieldInfo, String filePath, String charset) throws Exception;
}
