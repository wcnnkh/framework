package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;
import io.basc.framework.mapper.Field;

import java.nio.charset.Charset;

/**
 * 将内容解析为json
 * 
 * @author shuchaowen
 *
 */
public final class JsonFileValueProcessor extends AbstractObservableResourceValueProcessor {

	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource) {
		String content = ResourceUtils.getContent(resource, charset);
		if (JsonObject.class.isAssignableFrom(field.getSetter().getType())) {
			return JSONUtils.getJsonSupport().parseObject(content);
		} else if (JsonArray.class.isAssignableFrom(field.getSetter().getType())) {
			return JSONUtils.getJsonSupport().parseArray(content);
		} else if (String.class.isAssignableFrom(field.getSetter().getType())) {
			return content;
		} else {
			return JSONUtils.getJsonSupport().parseObject(content, field.getSetter().getType());
		}
	}
}
