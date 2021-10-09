package io.basc.framework.beans.ioc.value;

import io.basc.framework.beans.BeanDefinition;
import io.basc.framework.beans.BeanFactory;
import io.basc.framework.beans.annotation.Value;
import io.basc.framework.core.ResolvableType;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourceUtils;
import io.basc.framework.mapper.Field;
import io.basc.framework.value.StringValue;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class FileLinesValueProcessor extends AbstractObservableResourceValueProcessor {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory,
			Object bean, Field field, Value value, String name, Charset charset, Resource resource) throws Exception {
		List<String> lines = ResourceUtils.getLines(resource, charset);
		if (lines == null) {
			return null;
		}

		if (field.getSetter().getType().isArray()) {
			Object array = Array.newInstance(field.getSetter().getType().getComponentType(), lines.size());
			for (int i = 0; i < lines.size(); i++) {
				io.basc.framework.value.Value v = new StringValue(lines.get(i));
				Array.set(array, i, v.getAsObject(field.getSetter().getType().getComponentType()));
			}
			return array;
		} else if (Collection.class.isAssignableFrom(field.getSetter().getType())) {
			ResolvableType resolvableType = ResolvableType.forType(field.getSetter().getGenericType());
			ResolvableType componentType = resolvableType.getGeneric(0);
			List list = new ArrayList();
			for (String line : lines) {
				list.add(new StringValue(line).getAsObject(componentType.getType()));
			}
			return list;
		} else if (Set.class.isAssignableFrom(field.getSetter().getType())) {
			ResolvableType resolvableType = ResolvableType.forType(field.getSetter().getGenericType());
			ResolvableType componentType = resolvableType.getGeneric(0);
			Set set = new LinkedHashSet();
			for (String line : lines) {
				set.add(new StringValue(line).getAsObject(componentType.getType()));
			}
			return set;
		} else {
			StringBuilder sb = new StringBuilder();
			for (String str : lines) {
				sb.append(str);
			}
			return new StringValue(sb.toString()).getAsObject(field.getSetter().getGenericType());
		}
	}
}
