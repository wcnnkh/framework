package io.basc.framework.context.ioc.support;

import java.nio.charset.Charset;

import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.factory.BeanDefinition;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.json.JSONUtils;
import io.basc.framework.json.JsonArray;
import io.basc.framework.json.JsonObject;
import io.basc.framework.mapper.Field;

/**
 * 将内容解析为json
 * 
 * @author shuchaowen
 *
 */
public final class JsonFileValueProcessor extends AbstractObservableResourceValueProcessor {

	@Override
	protected Object parse(BeanDefinition beanDefinition, Context context, Object bean, Field field, ValueDefinition valueDefinition,
			String name, Charset charset, Resource resource) {
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