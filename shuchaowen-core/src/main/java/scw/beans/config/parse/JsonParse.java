package scw.beans.config.parse;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractCharsetNameValueFormat;
import scw.core.Constants;
import scw.core.reflect.FieldDefinition;
import scw.io.resource.ResourceUtils;
import scw.json.JSONUtils;
import scw.json.JsonArray;
import scw.json.JsonObject;
import scw.util.value.property.PropertyFactory;

/**
 * 将内容解析为json
 * 
 * @author shuchaowen
 *
 */
public final class JsonParse extends AbstractCharsetNameValueFormat implements ConfigParse {

	public JsonParse() {
		this(Constants.DEFAULT_CHARSET_NAME);
	}

	public JsonParse(String charsetName) {
		super(charsetName);
	}

	public Object parse(BeanFactory beanFactory, PropertyFactory propertyFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		String content = ResourceUtils.getResourceOperations().getFileContent(filePath, charset);
		if (JsonObject.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return JSONUtils.parseObject(content);
		} else if (JsonArray.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return JSONUtils.parseArray(content);
		} else if (String.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return content;
		} else {
			return JSONUtils.parseObject(content, fieldDefinition.getField().getType());
		}
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		String content = ResourceUtils.getResourceOperations().getFileContent(name, getCharsetName());
		if (JsonObject.class.isAssignableFrom(field.getType())) {
			return JSONUtils.parseObject(content);
		} else if (JsonArray.class.isAssignableFrom(field.getType())) {
			return JSONUtils.parseArray(content);
		} else if (String.class.isAssignableFrom(field.getType())) {
			return content;
		} else {
			return JSONUtils.parseObject(content, field.getType());
		}
	}

}
