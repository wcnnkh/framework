package io.basc.framework.core.convert.transform.mapping;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.stereotype.Accessor;
import io.basc.framework.core.convert.transform.stereotype.Template;
import io.basc.framework.core.convert.transform.stereotype.TemplateContext;
import io.basc.framework.core.convert.transform.stereotype.TemplateTransformer;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.alias.JoinNamingStrategy;
import io.basc.framework.util.alias.NamingStrategy;
import io.basc.framework.util.collections.Elements;
import lombok.NonNull;

/**
 * 配置属性
 * 
 * @author soeasy.run
 *
 */
public class ConfigurationProperties extends
		TemplateTransformer<Object, Accessor, Template<Object, ? extends Accessor>, Accessor, Template<Object, ? extends Accessor>, ConversionException> {
	@NonNull
	private NamingStrategy namingStrategy = new JoinNamingStrategy(".");

	@Override
	public Elements<? extends Accessor> readFrom(
			TemplateContext<Object, Accessor, Template<Object, ? extends Accessor>> sourceContext,
			@NonNull Template<Object, ? extends Accessor> source, @NonNull TypeDescriptor sourceType,
			TemplateContext<Object, Accessor, Template<Object, ? extends Accessor>> targetContext,
			@NonNull Template<Object, ? extends Accessor> target, @NonNull TypeDescriptor targetType,
			@NonNull Object index, @NonNull Accessor targetAccessor) throws ConversionException {
		Elements<? extends Accessor> elements = super.readFrom(sourceContext, source, sourceType, targetContext, target,
				targetType, index, targetAccessor);
		if (namingStrategy != null && index instanceof String) {
			elements = elements.toList();
			String prefix = (String) index;
			if (elements.isEmpty() && (targetAccessor.getRequiredTypeDescriptor().isMap()
					|| targetAccessor.getRequiredTypeDescriptor().isCollection())) {
				Elements<String> keys = source.getAccessorIndexes().filter((e) -> (e instanceof String))
						.map((e) -> (String) e)
						.filter((e) -> namingStrategy.test(e) && namingStrategy.startsWith(e, prefix)).toList();
				if (!keys.isEmpty()) {
					Elements<KeyValue<String, Accessor>> keyValues = keys.flatMap(
							(e) -> super.readFrom(sourceContext, source, sourceType, targetContext, target, targetType,
									e, targetAccessor).map((v) -> KeyValue.of(namingStrategy.display(e, prefix), v)));
					if (targetAccessor.getRequiredTypeDescriptor().isCollection()) {
						List<Accessor> list = keyValues.map((e) -> e.getValue()).toList();
						Accessor value = Accessor.of(Source.of(list));
						elements = Elements.singleton(value);
					} else if (targetAccessor.getRequiredTypeDescriptor().isMap()) {
						Map<String, List<Accessor>> map = new LinkedHashMap<>();
						for (KeyValue<String, Accessor> keyValue : keyValues) {
							List<Accessor> list = map.get(keyValue.getKey());
							if (list == null) {
								list = new ArrayList<>(2);
								map.put(keyValue.getKey(), list);
							}
							list.add(keyValue.getValue());
						}

						Map<String, Object> result = new LinkedHashMap<>();
						for (Entry<String, List<Accessor>> entry : map.entrySet()) {
							if (entry.getValue().size() == 1) {
								result.put(entry.getKey(), entry.getValue().get(0));
							} else {
								result.put(entry.getKey(), entry.getValue());
							}
						}
						Accessor value = Accessor.of(Source.of(result));
						return Elements.singleton(value);
					}
				}
			}
		}
		return elements;
	}
}
