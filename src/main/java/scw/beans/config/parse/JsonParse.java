package scw.beans.config.parse;

import java.lang.reflect.Field;

import scw.beans.BeanFactory;
import scw.beans.config.ConfigParse;
import scw.beans.property.AbstractCharsetNameValueFormat;
import scw.core.Constants;
import scw.core.PropertyFactory;
import scw.core.reflect.FieldDefinition;
import scw.core.utils.ConfigUtils;
import scw.json.JSONArray;
import scw.json.JSONObject;
import scw.json.JSONUtils;

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

	public Object parse(BeanFactory beanFactory, FieldDefinition fieldDefinition, String filePath, String charset)
			throws Exception {
		String content = ConfigUtils.getFileContent(filePath, charset);
		if (JSONObject.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return JSONUtils.parseObject(content);
		} else if (JSONArray.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return JSONUtils.parseArray(content);
		} else if (String.class.isAssignableFrom(fieldDefinition.getField().getType())) {
			return content;
		} else {
			return JSONUtils.parseObject(content, fieldDefinition.getField().getType());
		}
	}

	public Object format(BeanFactory beanFactory, PropertyFactory propertyFactory, Field field, String name)
			throws Exception {
		String content = ConfigUtils.getFileContent(name, getCharsetName());
		if (JSONObject.class.isAssignableFrom(field.getType())) {
			return JSONUtils.parseObject(content);
		} else if (JSONArray.class.isAssignableFrom(field.getType())) {
			return JSONUtils.parseArray(content);
		} else if (String.class.isAssignableFrom(field.getType())) {
			return content;
		} else {
			return JSONUtils.parseObject(content, field.getType());
		}
	}

}
