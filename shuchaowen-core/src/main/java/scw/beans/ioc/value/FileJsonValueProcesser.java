package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.json.JSONUtils;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.mapper.Field;
import scw.value.property.PropertyFactory;

/**
 * 将内容解析为json
 * 
 * @author shuchaowen
 *
 */
public final class FileJsonValueProcesser extends AbstractFileContentValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, String content) {
		if (JsonObject.class.isAssignableFrom(field.getSetter().getType())) {
			return JSONUtils.parseObject(content);
		} else if (JsonArray.class.isAssignableFrom(field.getSetter().getType())) {
			return JSONUtils.parseArray(content);
		} else if (String.class.isAssignableFrom(field.getSetter().getType())) {
			return content;
		} else {
			return JSONUtils.parseObject(content, field.getSetter().getType());
		}
	}
}
