package io.basc.framework.context.ioc.support;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.context.ioc.ValueDefinition;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.mapper.Field;
import io.basc.framework.value.Value;

public final class FileLinesValueProcessor extends AbstractObservableResourceValueProcessor {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object parse(BeanDefinition beanDefinition, Context context, Object bean, Field field,
			ValueDefinition valueDefinition, String name, Charset charset, Resource resource) throws Exception {
		List<String> lines = ResourceUtils.readLines(resource, charset).toList();
		if (lines == null) {
			return null;
		}

		if (field.getSetter().getType().isArray()) {
			Object array = Array.newInstance(field.getSetter().getType().getComponentType(), lines.size());
			for (int i = 0; i < lines.size(); i++) {
				io.basc.framework.value.Value v = Value.of(lines.get(i));
				Array.set(array, i, v.getAsObject(field.getSetter().getType().getComponentType()));
			}
			return array;
		} else if (Collection.class.isAssignableFrom(field.getSetter().getType())) {
			ResolvableType resolvableType = ResolvableType.forType(field.getSetter().getGenericType());
			ResolvableType componentType = resolvableType.getGeneric(0);
			List list = new ArrayList();
			for (String line : lines) {
				list.add(Value.of(line).getAsObject(componentType));
			}
			return list;
		} else if (Set.class.isAssignableFrom(field.getSetter().getType())) {
			ResolvableType resolvableType = ResolvableType.forType(field.getSetter().getGenericType());
			ResolvableType componentType = resolvableType.getGeneric(0);
			Set set = new LinkedHashSet();
			for (String line : lines) {
				set.add(Value.of(line).getAsObject(componentType.getType()));
			}
			return set;
		} else {
			StringBuilder sb = new StringBuilder();
			for (String str : lines) {
				sb.append(str);
			}
			return Value.of(sb.toString()).getAsObject(field.getSetter().getGenericType());
		}
	}
}
