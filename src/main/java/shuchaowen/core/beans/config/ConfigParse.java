package shuchaowen.core.beans.config;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.util.FieldInfo;


/**
 * 解析配置文件
 * @author shuchaowen
 *
 */
public interface ConfigParse {
	Object parse(BeanFactory beanFactory, FieldInfo fieldInfo, String filePath, String charset) throws Exception;
}
