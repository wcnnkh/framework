package scw.beans.ioc.value;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import scw.beans.BeanDefinition;
import scw.beans.BeanFactory;
import scw.beans.annotation.Value;
import scw.core.ResolvableType;
import scw.mapper.Field;
import scw.value.StringValue;
import scw.value.property.PropertyFactory;

public final class FileLinesValueProcesser extends AbstractFileLinesValueProcesser {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object parse(BeanDefinition beanDefinition, BeanFactory beanFactory, PropertyFactory propertyFactory,
			Object bean, Field field, Value value, String name, String charsetName, List<String> lines) {
		if (lines == null) {
			return null;
		}

		if (field.getSetter().getType().isArray()) {
			Object array = Array.newInstance(field.getSetter().getType().getComponentType(), lines.size());
			for (int i = 0; i < lines.size(); i++) {
				scw.value.Value v = new StringValue(lines.get(i));
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
