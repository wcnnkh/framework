package scw.beans.config.parse;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractValueFormat;
import scw.io.ResourceUtils;
import scw.json.JSONUtils;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.mapper.FieldContext;
import scw.util.value.property.PropertyFactory;

/**
 * 将内容解析为json
 * 
 * @author shuchaowen
 *
 */
public final class JsonParse extends AbstractValueFormat implements ConfigParse {

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext, String filePath, String charset)
			throws Exception {
		String content = ResourceUtils.getResourceOperations().getContent(filePath, charset);
		if (JsonObject.class.isAssignableFrom(fieldContext.getField().getSetter().getType())) {
			return JSONUtils.parseObject(content);
		} else if (JsonArray.class.isAssignableFrom(fieldContext.getField().getSetter().getType())) {
			return JSONUtils.parseArray(content);
		} else if (String.class.isAssignableFrom(fieldContext.getField().getSetter().getType())) {
			return content;
		} else {
			return JSONUtils.parseObject(content, fieldContext.getField().getSetter().getType());
		}
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldContext fieldContext, String name)
			throws Exception {
		return parse(beanFactory, propertyFactory, fieldContext, name, getCharsetName());
	}

}
