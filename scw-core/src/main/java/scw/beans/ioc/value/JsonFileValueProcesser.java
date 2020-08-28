package scw.beans.ioc.value;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.io.Resource;
import scw.io.ResourceUtils;
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
public final class JsonFileValueProcesser extends AbstractObservableResourceValueProcesser {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, Resource resource) {
		String content = ResourceUtils.getContent(resource, charsetName);
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
