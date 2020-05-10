package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.io.ResourceUtils;
import scw.json.JSONUtils;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.mapper.Field;
import scw.util.value.property.PropertyFactory;

/**
 * 将内容解析为json
 * 
 * @author shuchaowen
 *
 */
public final class JsonParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String filePath, String charset)
			throws Exception {
		String content = ResourceUtils.getResourceOperations().getContent(filePath, charset);
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

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, field, name, getCharsetName());
	}

}
